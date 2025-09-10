package com.minddigest.backend.config;

import com.minddigest.backend.crawler.CrawlerAdapterFactory;
import com.minddigest.backend.crawler.InMemoryCrawlerRegistry;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import com.minddigest.backend.crawler.interfaces.CrawlerRegistry;
import com.minddigest.backend.crawler.interfaces.CrawlerComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;

@Configuration
public class CrawlerConfig {

    private final CrawlerAdapterFactory adapterFactory;

    public CrawlerConfig(CrawlerAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    @Bean
    public CrawlerRegistry crawlerRegistry(List<CrawlerPageProcessor> processors) {
        InMemoryCrawlerRegistry registry = new InMemoryCrawlerRegistry();

        for (CrawlerPageProcessor proc : processors) {
            CrawlerComponent annotation = AnnotationUtils.findAnnotation(proc.getClass(), CrawlerComponent.class);
            if (annotation != null) {
                String domain = annotation.domain().trim().toLowerCase();
                registry.registerCrawler(domain, adapterFactory.createAdapter(proc));
            }
        }
        return registry;
    }
}
