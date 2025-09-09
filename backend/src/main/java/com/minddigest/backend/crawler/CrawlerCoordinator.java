package com.minddigest.backend.crawler;

import com.minddigest.backend.config.CrawlerProperties;
import com.minddigest.backend.dto.DigestEntryDto;
import com.minddigest.backend.crawler.interfaces.CrawlerRegistry;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Service coordinating the execution of multiple web crawlers concurrently.
 * <p>
 * Uses a thread pool to run crawler tasks in parallel based on configured sites and thread count.
 * Collects results from all crawlers and handles exceptions during crawling.
 * Provides a graceful shutdown mechanism to stop the executor service.
 * </p>
 */
@Service
public class CrawlerCoordinator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerCoordinator.class);

    private final CrawlerRegistry crawlerRegistry;

    private final CrawlerProperties crawlerProperties;

    private final ExecutorService executorService;

    /**
     * Constructs a new CrawlerCoordinator instance.
     *
     * @param crawlerRegistry   registry to retrieve crawler implementations by domain
     * @param crawlerProperties configuration properties including sites and thread count
     */
    @Autowired
    public CrawlerCoordinator(CrawlerRegistry crawlerRegistry, CrawlerProperties crawlerProperties) {
        this.crawlerRegistry = crawlerRegistry;
        this.crawlerProperties = crawlerProperties;
        // Use configured thread count or number of sites as thread pool size
        int threadPoolSize = Math.max(
                crawlerProperties.getThreads(),
                Math.max(2, crawlerProperties.getSites().size())
        );
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Starts crawling all configured sites concurrently.
     * <p>
     * For each site configured in {@code crawlerProperties}, this method:
     * <ul>
     *   <li>Retrieves the appropriate crawler from the registry by domain.</li>
     *   <li>Initializes the crawler with domain and start URL.</li>
     *   <li>Executes the crawling task asynchronously using a thread pool.</li>
     *   <li>Aggregates all result's into a single list.</li>
     * </ul>
     * If no crawler is found for a domain, an {@link IllegalArgumentException} is thrown.
     *
     * @return a combined list of {@link DigestEntryDto} results from all crawlers
     */
    public List<DigestEntryDto> startAllCrawlers() {
        List<Callable<List<DigestEntryDto>>> tasks = crawlerProperties.getSites().stream()
                .map(site -> (Callable<List<DigestEntryDto>>) () ->
                        crawlerRegistry.getCrawlerForDomain(site.getDomain())
                                .map(crawler -> {
                                    crawler.init(site.getDomain(), site.getStartUrl());
                                    LOGGER.info("Starting crawler for domain {}", site.getDomain());
                                    return crawler.crawl();
                                })
                                .orElseThrow(() -> new IllegalArgumentException("No crawler found for domain: " + site.getDomain()))
                )
                .toList();

        List<DigestEntryDto> allResults = new ArrayList<>();
        try {
            List<Future<List<DigestEntryDto>>> futures = executorService.invokeAll(tasks);

            for (Future<List<DigestEntryDto>> future : futures) {
                allResults.addAll(getCrawlerResultsFromFuture(future));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("CrawlerCoordinator interrupted", e);
        }
        LOGGER.info("All crawlers finished, total results: {}", allResults.size());
        return allResults;
    }

    /**
     * Extracts the crawler results from the Future, handling exceptions properly.
     *
     * @param future Future containing the crawl results.
     * @return list of {@link DigestEntryDto} results, or empty list on error.
     */
    private List<DigestEntryDto> getCrawlerResultsFromFuture(Future<List<DigestEntryDto>> future) {
        try {
            return future.get();
        } catch (ExecutionException e) {
            LOGGER.error("Error during crawler execution", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Crawler execution interrupted", e);
        }
        return Collections.emptyList();
    }

    /**
     * Shuts down the executor service gracefully.
     * <p>
     * Waits for running tasks to finish or times out after 30 seconds,
     * then forces shutdown if necessary.
     * </p>
     */
    @PreDestroy
    public void shutdown() {
        LOGGER.info("Shutting down CrawlerCoordinator executor service...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                LOGGER.warn("Executor did not terminate in the specified time. Forcing shutdown...");
                executorService.shutdownNow();
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    LOGGER.error("Executor did not terminate after forced shutdown");
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("Shutdown interrupted, forcing shutdown now", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("CrawlerCoordinator executor service shut down");
    }
}
