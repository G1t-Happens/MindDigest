package com.minddigest.backend.crawler;

import com.minddigest.backend.config.CrawlerProperties;
import com.minddigest.backend.crawler.interfaces.Crawler;
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
     * Starts crawling processes for all configured sites concurrently.
     * <p>
     * For each site defined in {@code crawlerProperties}, this method gets the appropriate crawler
     * from the {@code crawlerRegistry}, initializes it with the domain and start URL, and then executes the crawl.
     * The crawling tasks run in parallel using the configured {@code executorService}.
     * <p>
     * If any crawler is not found for a domain, an {@link IllegalArgumentException} is thrown.
     * Interrupted exceptions during the execution of tasks are properly handled by
     * interrupting the current thread and logging the error.
     * <p>
     * All results from the crawlers are collected and returned as a combined list of {@link DigestEntryDto}.
     *
     * @return a combined list of all crawl results from all sites
     * @throws IllegalArgumentException if a crawler cannot be found for any configured domain
     */
    public List<DigestEntryDto> startAllCrawlers() {
        List<Callable<List<DigestEntryDto>>> tasks = createCrawlerTasks();

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
     * Creates a list of {@link Callable} tasks, each responsible for running
     * the crawl process on a specific site.
     * <p>
     * Each task:
     * <ul>
     *   <li>Retrieves the {@link Crawler} instance for the site's domain from {@code crawlerRegistry}.</li>
     *   <li>Initializes the crawler with the domain and start URL.</li>
     *   <li>Executes the crawl and returns the list of {@link DigestEntryDto} results.</li>
     * </ul>
     * <p>
     * If no crawler is found for a domain, the task will throw an {@link IllegalArgumentException}
     * when executed.
     *
     * @return a list of callable tasks to be executed concurrently for all configured sites
     * @throws IllegalArgumentException if a crawler cannot be found for any configured domain when tasks are executed
     */
    private List<Callable<List<DigestEntryDto>>> createCrawlerTasks() {
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
        return tasks;
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
