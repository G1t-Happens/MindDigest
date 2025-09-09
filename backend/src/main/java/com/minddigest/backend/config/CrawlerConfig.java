package com.minddigest.backend.config;

import com.minddigest.backend.crawler.InMemoryCrawlerRegistry;
import com.minddigest.backend.crawler.WebMagicCrawlerAdapter;
import com.minddigest.backend.crawler.interfaces.CrawlerComponent;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import com.minddigest.backend.crawler.interfaces.CrawlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration class responsible for setting up the crawler registry bean.
 * <p>
 * This configuration scans the Spring application context for beans implementing
 * {@link CrawlerPageProcessor} annotated with {@link CrawlerComponent} and
 * registers them in an {@link InMemoryCrawlerRegistry} using their domain as a key.
 * The registered crawlers are adapted via {@link WebMagicCrawlerAdapter} and
 * configured with the thread count from {@link CrawlerProperties}.
 * </p>
 */
@Configuration
public class CrawlerConfig {

    /**
     * Properties containing crawler-related configuration such as thread count.
     */
    private final CrawlerProperties crawlerProperties;

    /**
     * Constructs a new {@code CrawlerConfig} instance with injected crawler properties.
     *
     * @param crawlerProperties the configuration properties for crawlers
     */
    @Autowired
    public CrawlerConfig(CrawlerProperties crawlerProperties) {
        this.crawlerProperties = crawlerProperties;
    }

    /**
     * Creates and configures the {@link CrawlerRegistry} bean.
     * <p>
     * This method scans the Spring application context for all beans implementing
     * {@link CrawlerPageProcessor}. For each such bean annotated with
     * {@link CrawlerComponent}, it registers a crawler adapter in the registry
     * keyed by the crawler's domain name (converted to lowercase).
     * The crawler adapter is instantiated with the corresponding processor
     * and the configured number of threads.
     * </p>
     *
     * @param ctx the Spring application context used to discover crawler beans
     * @return the configured {@link CrawlerRegistry} instance
     */
    @Bean
    public CrawlerRegistry crawlerRegistry(ApplicationContext ctx) {
        InMemoryCrawlerRegistry registry = new InMemoryCrawlerRegistry();

        Map<String, CrawlerPageProcessor> beans = ctx.getBeansOfType(CrawlerPageProcessor.class);
        for (CrawlerPageProcessor proc : beans.values()) {
            CrawlerComponent annotation = proc.getClass().getAnnotation(CrawlerComponent.class);
            if (annotation != null) {
                registry.registerCrawler(annotation.domain().toLowerCase(), new WebMagicCrawlerAdapter(proc, crawlerProperties.getThreads()));
            }
        }
        return registry;
    }
}
