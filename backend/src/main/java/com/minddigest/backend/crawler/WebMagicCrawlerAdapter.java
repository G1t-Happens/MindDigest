package com.minddigest.backend.crawler;

import com.minddigest.backend.dto.DigestEntryDto;
import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * Adapter class that bridges the generic {@link Crawler} interface
 * with the WebMagic framework's {@link CrawlerPageProcessor}.
 * <p>
 * This class initializes and runs the WebMagic {@link Spider} with
 * the configured {@link CrawlerPageProcessor} and manages crawling threads.
 * </p>
 */
@Component
public class WebMagicCrawlerAdapter implements Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebMagicCrawlerAdapter.class);

    /**
     * The underlying WebMagic PageProcessor implementation.
     */
    private final CrawlerPageProcessor processor;

    /**
     * The start URL for the crawl session.
     */
    private String startUrl;

    /**
     * Number of threads used by the WebMagic Spider.
     */
    private final int threadCount;

    /**
     * Constructs the adapter with the given PageProcessor and thread count.
     *
     * @param processor   the WebMagic-based page processor
     * @param threadCount number of threads to run concurrently (default 4)
     */
    @Autowired
    public WebMagicCrawlerAdapter(CrawlerPageProcessor processor, @Value("${crawler.threadCount:4}") int threadCount) {
        this.processor = processor;
        this.threadCount = threadCount;
    }

    /**
     * Initializes the crawler with the domain and starting URL.
     * Delegates initialization to the underlying processor.
     *
     * @param domain   target domain for crawling
     * @param startUrl initial URL to begin crawling
     */
    @Override
    public void init(String domain, String startUrl) {
        this.startUrl = startUrl;
        processor.init(domain, startUrl);
        LOGGER.info("Initialized crawler for domain '{}' with startUrl '{}'", domain, startUrl);
    }

    /**
     * Executes the crawling process using WebMagic's Spider.
     * <p>
     * Starts the Spider with the configured processor, start URL, and thread count.
     * Exceptions during crawling are caught and logged.
     * After completion, returns the collected results.
     * </p>
     *
     * @return list of {@link DigestEntryDto} extracted during crawl
     */
    @Override
    public List<DigestEntryDto> crawl() {
        LOGGER.info("Starting crawl for URL: {}", startUrl);

        try {
            Spider.create(processor)
                    .addUrl(startUrl)
                    .thread(threadCount)
                    .run();
        } catch (Exception e) {
            LOGGER.error("Error during crawling", e);
        }

        List<DigestEntryDto> results = processor.getResults();
        LOGGER.info("Crawl finished, found {} results", results.size());
        return results;
    }
}
