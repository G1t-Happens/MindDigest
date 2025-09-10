package com.minddigest.backend.crawler.adapters.webmagic;

import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerAdapterBuilder;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import org.springframework.stereotype.Component;


/**
 * {@link CrawlerAdapterBuilder} implementation for creating {@link WebMagicCrawlerAdapter} instances.
 * <p>
 * This builder creates a {@link Crawler} using the WebMagic framework with the specified
 * {@link CrawlerPageProcessor} and thread count.
 * </p>
 * <p>
 * Registered as a Spring bean with the qualifier "webMagicAdapter" for injection and discovery.
 * </p>
 */
@Component("webMagicAdapter")
public class WebMagicAdapterBuilder implements CrawlerAdapterBuilder {

    /**
     * Builds a {@link WebMagicCrawlerAdapter} instance with the given page processor and number of threads.
     *
     * @param processor the page processor to use for crawling; must not be {@code null}
     * @param threads   the number of threads to be used by the crawler; must be a positive integer
     * @return a new instance of {@link WebMagicCrawlerAdapter} configured with the specified processor and threads
     * @throws IllegalArgumentException if {@code processor} is {@code null} or {@code threads} is not positive
     */
    @Override
    public Crawler build(CrawlerPageProcessor processor, int threads) {
        if (processor == null) {
            throw new IllegalArgumentException("Processor must not be null");
        }
        if (threads <= 0) {
            throw new IllegalArgumentException("Thread count must be positive");
        }
        return new WebMagicCrawlerAdapter(processor, threads);
    }
}
