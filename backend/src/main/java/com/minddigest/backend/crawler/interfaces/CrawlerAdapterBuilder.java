package com.minddigest.backend.crawler.interfaces;


/**
 * Builder interface for creating {@link Crawler} instances adapted to specific
 * {@link CrawlerPageProcessor} implementations.
 * <p>
 * This abstraction allows decoupling the construction logic of various crawler
 * adapters, enabling flexible creation with custom configurations such as
 * thread count.
 * </p>
 */
public interface CrawlerAdapterBuilder {

    /**
     * Builds a {@link Crawler} instance for the given {@link CrawlerPageProcessor} with the specified
     * number of threads to be used during crawling.
     *
     * @param processor the page processor responsible for processing pages during the crawl; must not be {@code null}
     * @param threads   the number of threads that the resulting crawler should use; must be a positive integer
     * @return a fully initialized {@link Crawler} instance configured with the given processor and thread count
     * @throws IllegalArgumentException if {@code processor} is {@code null} or {@code threads} is not positive
     */
    Crawler build(CrawlerPageProcessor processor, int threads);
}
