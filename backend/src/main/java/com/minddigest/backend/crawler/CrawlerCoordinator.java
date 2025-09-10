package com.minddigest.backend.crawler;

import com.minddigest.backend.config.CrawlerProperties;
import com.minddigest.backend.crawler.interfaces.CrawlerRegistry;
import com.minddigest.backend.dto.DigestEntryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;


/**
 * Coordinates the concurrent execution of multiple crawlers based on configured sites.
 * <p>
 * This service initializes and starts crawlers for each configured domain using a thread pool,
 * collects their results, and handles graceful shutdown of the executor service.
 * </p>
 */
@Service
public class CrawlerCoordinator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerCoordinator.class);

    private final CrawlerRegistry crawlerRegistry;

    private final CrawlerProperties crawlerProperties;

    private final ExecutorService executorService;

    /**
     * Constructs the coordinator with the given crawler registry and properties.
     * Initializes a fixed thread pool sized based on the configured thread count and number of sites.
     *
     * @param crawlerRegistry   the registry providing crawlers per domain
     * @param crawlerProperties configuration properties including thread count and site list
     */
    public CrawlerCoordinator(CrawlerRegistry crawlerRegistry, CrawlerProperties crawlerProperties) {
        this.crawlerRegistry = crawlerRegistry;
        this.crawlerProperties = crawlerProperties;
        int threadPoolSize = Math.max(crawlerProperties.getThreads(), Math.max(2, crawlerProperties.getSites().size()));
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        LOGGER.info("[COORDINATOR] Thread pool created with {} threads", threadPoolSize);
    }

    /**
     * Starts crawlers concurrently for all configured sites and collects their results.
     * <p>
     * Each site triggers retrieval of the matching crawler which is then initialized and executed.
     * Results from all crawlers are aggregated into a single list.
     * </p>
     *
     * @return list of all collected {@link DigestEntryDto} from all crawlers
     * @throws IllegalArgumentException if no crawler is found for a configured domain
     */
    public List<DigestEntryDto> startAllCrawlers() {
        List<Callable<List<DigestEntryDto>>> tasks = new ArrayList<>();
        for (var site : crawlerProperties.getSites()) {
            tasks.add(() -> crawlerRegistry.getCrawlerForDomain(site.getDomain())
                    .map(crawler -> {
                        LOGGER.info("[COORDINATOR] Starting crawler for domain {}", site.getDomain());
                        crawler.init(site.getDomain(), site.getStartUrl());
                        return crawler.crawl();
                    })
                    .orElseThrow(() -> new IllegalArgumentException("No crawler found for domain: " + site.getDomain()))
            );
        }

        List<DigestEntryDto> allResults = new ArrayList<>();
        try {
            List<Future<List<DigestEntryDto>>> futures = executorService.invokeAll(tasks);
            for (Future<List<DigestEntryDto>> future : futures) {
                allResults.addAll(getResultsSafely(future));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("[COORDINATOR] Crawling interrupted", e);
        }

        LOGGER.info("[COORDINATOR] Finished all crawlers. Total results: {}", allResults.size());
        return allResults;
    }

    /**
     * Retrieves crawler results from the given future, handling exceptions and interruptions.
     *
     * @param future the future representing the crawler task
     * @return list of digest entries, or empty list if execution failed or was interrupted
     */
    private List<DigestEntryDto> getResultsSafely(Future<List<DigestEntryDto>> future) {
        try {
            return future.get();
        } catch (ExecutionException e) {
            LOGGER.error("[COORDINATOR] Error during crawler execution", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("[COORDINATOR] Crawler execution interrupted", e);
        }
        return Collections.emptyList();
    }

    /**
     * Gracefully shuts down the executor service managing crawler threads.
     * <p>
     * Attempts an orderly shutdown, waiting up to 30 seconds for tasks to finish.
     * If not terminated in time, forces shutdown and logs accordingly.
     * </p>
     */
    @PreDestroy
    public void shutdown() {
        LOGGER.info("[COORDINATOR] Shutting down executor service...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                LOGGER.warn("[COORDINATOR] Executor did not terminate in time; forcing shutdown");
                executorService.shutdownNow();
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    LOGGER.error("[COORDINATOR] Executor did not terminate after forced shutdown");
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("[COORDINATOR] Shutdown interrupted; forcing shutdown now", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("[COORDINATOR] Executor service shut down.");
    }
}
