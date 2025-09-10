package com.minddigest.backend.config;

import com.minddigest.backend.crawler.CrawlerAdapterFactory;
import com.minddigest.backend.crawler.InMemoryCrawlerRegistry;
import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import com.minddigest.backend.crawler.interfaces.CrawlerRegistry;
import com.minddigest.backend.crawler.interfaces.CrawlerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;


/**
 * Spring configuration class responsible for wiring and configuring the crawler-related components.
 * <p>
 * This configuration class provides a {@link CrawlerRegistry} bean that manages the registration
 * of {@link Crawler} instances, each associated with a specific domain.
 * </p>
 * <p>
 * The registry is initialized by scanning the available {@link CrawlerPageProcessor} beans,
 * extracting their domain information via the {@link CrawlerComponent} annotation,
 * and creating the appropriate crawler adapters through the {@link CrawlerAdapterFactory}.
 * </p>
 */
@Configuration
public class CrawlerConfig {

    private final CrawlerAdapterFactory adapterFactory;

    private final CrawlerProperties crawlerProperties;

    /**
     * Constructs the configuration with required dependencies injected by Spring.
     *
     * @param adapterFactory    factory to create crawler adapters for processors
     * @param crawlerProperties configuration properties containing settings such as thread count
     */
    @Autowired
    public CrawlerConfig(CrawlerAdapterFactory adapterFactory, CrawlerProperties crawlerProperties) {
        this.adapterFactory = adapterFactory;
        this.crawlerProperties = crawlerProperties;
    }

    /**
     * Creates and initializes the {@link CrawlerRegistry} bean.
     * <p>
     * Iterates over all registered {@link CrawlerPageProcessor} beans,
     * discovers their associated domains from the {@link CrawlerComponent} annotation,
     * and creates domain-specific {@link Crawler} instances via the adapter factory.
     * These crawlers are then registered in an in-memory registry implementation.
     * </p>
     * <p>
     * The configured thread count from {@link CrawlerProperties} is passed to each crawler during creation.
     * </p>
     *
     * @param processors the list of all available {@link CrawlerPageProcessor} beans in the Spring context
     * @return a fully populated {@link CrawlerRegistry} managing crawlers by domain
     */
    @Bean
    public CrawlerRegistry crawlerRegistry(List<CrawlerPageProcessor> processors) {
        InMemoryCrawlerRegistry registry = new InMemoryCrawlerRegistry();

        for (CrawlerPageProcessor proc : processors) {
            CrawlerComponent annotation = AnnotationUtils.findAnnotation(proc.getClass(), CrawlerComponent.class);
            if (annotation != null) {
                String domain = annotation.domain().trim().toLowerCase();
                int threadCount = crawlerProperties.getThreads();
                Crawler crawler = adapterFactory.createAdapter(proc, threadCount);
                registry.registerCrawler(domain, crawler);
            }
        }
        return registry;
    }
}
