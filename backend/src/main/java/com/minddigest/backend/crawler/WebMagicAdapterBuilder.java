package com.minddigest.backend.crawler;

import com.minddigest.backend.config.CrawlerProperties;
import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerAdapterBuilder;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import org.springframework.stereotype.Component;

@Component
public class WebMagicAdapterBuilder implements CrawlerAdapterBuilder {

    private final CrawlerProperties crawlerProperties;

    public WebMagicAdapterBuilder(CrawlerProperties crawlerProperties) {
        this.crawlerProperties = crawlerProperties;
    }

    @Override
    public Crawler build(CrawlerPageProcessor processor) {
        return new WebMagicCrawlerAdapter(processor, crawlerProperties.getThreads());
    }
}

