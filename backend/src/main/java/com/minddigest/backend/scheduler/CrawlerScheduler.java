package com.minddigest.backend.scheduler;

import com.minddigest.backend.crawler.CrawlerCoordinator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scheduler component that automatically triggers the crawler every hour.
 * <p>
 * Uses Spring's {@link Scheduled} annotation to run the crawling job
 * at the start of every hour (minute 0).
 * </p>
 */
@Component
public class CrawlerScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerScheduler.class);

    private final CrawlerCoordinator crawlerCoordinator;

    /**
     * Constructor for injecting the {@link CrawlerCoordinator}.
     *
     * @param crawlerCoordinator coordinator responsible for starting all registered crawlers
     */
    @Autowired
    public CrawlerScheduler(CrawlerCoordinator crawlerCoordinator) {
        this.crawlerCoordinator = crawlerCoordinator;
    }

    /**
     * Executes the crawling process every hour at minute 0.
     * <p>
     * The cron expression {@code "0 0 * * * *"} means:
     * <ul>
     *   <li>Second 0</li>
     *   <li>Minute 0</li>
     *   <li>Every hour</li>
     *   <li>Every day, month, and weekday</li>
     * </ul>
     * </p>
     * <p>
     * This method triggers all registered crawlers and logs
     * the start and completion of the crawling task.
     * </p>
     */
    @Scheduled(cron = "0 * * * * *")
    public void runCrawlerHourly() {
        LOGGER.info("Starting scheduled crawler job");
        crawlerCoordinator.startAllCrawlers();
        LOGGER.info("Finished scheduled crawler job");
    }
}
