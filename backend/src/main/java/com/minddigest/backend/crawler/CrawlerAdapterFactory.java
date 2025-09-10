package com.minddigest.backend.crawler;

import com.minddigest.backend.config.CrawlerProperties;
import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import org.springframework.stereotype.Component;

@Component
public class CrawlerAdapterFactory {

    private final CrawlerProperties crawlerProperties;

    public CrawlerAdapterFactory(CrawlerProperties crawlerProperties) {
        this.crawlerProperties = crawlerProperties;
    }

    public Crawler createAdapter(CrawlerPageProcessor processor) {
        return new WebMagicCrawlerAdapter(processor, crawlerProperties.getThreads());
    }
}
