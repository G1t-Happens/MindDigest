package com.minddigest.backend.crawler.interfaces;

@FunctionalInterface
public interface CrawlerAdapterBuilder {
    Crawler build(CrawlerPageProcessor processor);
}

