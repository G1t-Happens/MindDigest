package com.minddigest.backend.crawler;

import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of {@link CrawlerRegistry}.
 * <p>
 * Maintains a mapping of domain names to corresponding {@link Crawler} instances.
 * Allows registering and retrieving crawlers by domain.
 * </p>
 */
@Component
public class InMemoryCrawlerRegistry implements CrawlerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCrawlerRegistry.class);

    /**
     * Internal map storing domain-to-crawler associations.
     * Uses a concurrent hash map for thread safety.
     */
    private final Map<String, Crawler> crawlersByDomain = new ConcurrentHashMap<>();

    /**
     * Retrieves the {@link Crawler} registered for the given domain.
     *
     * @param domain the domain name to look up; case-insensitive
     * @return an {@link Optional} containing the crawler if found, or empty if none registered or domain is null
     */
    @Override
    public Optional<Crawler> getCrawlerForDomain(String domain) {
        if (domain == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(crawlersByDomain.get(domain.toLowerCase()));
    }

    /**
     * Registers a {@link Crawler} instance for the specified domain.
     * <p>
     * Both domain and crawler must be non-null.
     * The domain is normalized to lowercase before storing.
     * </p>
     *
     * @param domain  the domain name to associate with the crawler; case-insensitive
     * @param crawler the crawler instance to register
     * @throws IllegalArgumentException if domain or crawler is null
     */
    @Override
    public void registerCrawler(String domain, Crawler crawler) {
        if (domain == null || crawler == null) {
            throw new IllegalArgumentException("Domain and crawler must not be null");
        }
        crawlersByDomain.put(domain.toLowerCase(), crawler);
        LOGGER.info("Registered crawler for domain: {}", domain);
    }
}
