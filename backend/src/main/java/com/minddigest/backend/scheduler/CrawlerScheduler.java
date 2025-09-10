package com.minddigest.backend.scheduler;

import com.minddigest.backend.crawler.CrawlerCoordinator;
import com.minddigest.backend.dto.DigestEntryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class CrawlerScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerScheduler.class);

    private final CrawlerCoordinator crawlerCoordinator;


    public CrawlerScheduler(CrawlerCoordinator crawlerCoordinator) {
        this.crawlerCoordinator = crawlerCoordinator;
    }

    @Scheduled(cron = "0 * * * * *")
    public void runCrawlerHourly() {
        LOGGER.info("[SCHEDULER] Starting scheduled crawler job");
        List<DigestEntryDto> results = crawlerCoordinator.startAllCrawlers();
        LOGGER.info("[SCHEDULER] Scheduled crawling job finished: {} results", results.size());
    }
}
