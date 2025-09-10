package com.minddigest.backend.scheduler;

import com.minddigest.backend.crawler.CrawlerCoordinator;
import com.minddigest.backend.dto.DigestEntryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Scheduler component responsible for periodically triggering the crawling process.
 * <p>
 * Uses Spring's scheduling support to run the crawler coordinator at fixed intervals,
 * collecting and logging the crawl results.
 * </p>
 */
@Component
public class CrawlerScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerScheduler.class);

    private final CrawlerCoordinator crawlerCoordinator;

    /**
     * Constructs a scheduler with the given crawler coordinator.
     *
     * @param crawlerCoordinator the coordinator that manages crawler execution
     */
    public CrawlerScheduler(CrawlerCoordinator crawlerCoordinator) {
        this.crawlerCoordinator = crawlerCoordinator;
    }

    /**
     * Scheduled method that runs every minute at second 0 according to the cron expression.
     * <p>
     * Invokes the crawler coordinator to start all configured crawlers and logs the total results.
     * </p>
     */
    @Scheduled(cron = "0 * * * * *")
    public void runCrawlerHourly() {
        LOGGER.info("[SCHEDULER] Starting scheduled crawler job");
        List<DigestEntryDto> results = crawlerCoordinator.startAllCrawlers();
        LOGGER.info("[SCHEDULER] Scheduled crawling job finished: {} results", results.size());
    }
}
