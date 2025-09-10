package com.minddigest.backend.crawler;

import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import com.minddigest.backend.dto.DigestEntryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.util.List;


/**
 * Adapter class that integrates a {@link CrawlerPageProcessor} with the WebMagic framework.
 * <p>
 * Manages initialization and execution of the crawl process using WebMagic's {@link Spider}.
 * Supports configurable thread count for concurrent crawling.
 * </p>
 */
public class WebMagicCrawlerAdapter implements Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebMagicCrawlerAdapter.class);

    /**
     * The page processor used to process and extract data during crawling.
     */
    private final CrawlerPageProcessor processor;

    /**
     * Number of threads to use for the crawl.
     */
    private final int threadCount;

    /**
     * Starting URL for the crawl.
     */
    private String startUrl;

    /**
     * Constructs the adapter with the given processor and thread count.
     *
     * @param processor   the page processor to delegate crawling tasks
     * @param threadCount the number of threads to run concurrently
     */
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
