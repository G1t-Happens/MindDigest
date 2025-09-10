package com.minddigest.backend.crawler;

import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import com.minddigest.backend.dto.DigestEntryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.util.List;


public class WebMagicCrawlerAdapter implements Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebMagicCrawlerAdapter.class);

    private final CrawlerPageProcessor processor;

    private final int threadCount;

    private String startUrl;


    public WebMagicCrawlerAdapter(CrawlerPageProcessor processor, int threadCount) {
        this.processor = processor;
        this.threadCount = threadCount;
    }

    @Override
    public void init(String domain, String startUrl) {
        this.startUrl = startUrl;
        processor.init(domain, startUrl);
        LOGGER.info("[CRAWLER-ADAPTER] Initialized crawler for domain '{}' with start URL '{}'", domain, startUrl);
    }

    @Override
    public List<DigestEntryDto> crawl() {
        LOGGER.info("[CRAWLER-ADAPTER] Starting crawl for URL: {}", startUrl);
        try {
            Spider.create(processor)
                    .addUrl(startUrl)
                    .thread(threadCount)
                    .run();
        } catch (Exception e) {
            LOGGER.error("[CRAWLER-ADAPTER] Error during crawling", e);
        }
        List<DigestEntryDto> results = processor.getResults();
        LOGGER.info("[CRAWLER-ADAPTER] Crawl finished, found {} results", results.size());
        return results;
    }
}
