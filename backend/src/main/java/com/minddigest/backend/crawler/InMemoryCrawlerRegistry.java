package com.minddigest.backend.crawler;

import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Thread-safe in-memory registry for managing crawlers keyed by domain name.
 * <p>
 * Provides lookup and registration of {@link Crawler} instances by normalized domain strings.
 * Domains are normalized by trimming and converting to lowercase using {@link Locale#ROOT}.
 * </p>
 */
@Component
public class InMemoryCrawlerRegistry implements CrawlerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCrawlerRegistry.class);

    /**
     * Internal map storing crawlers indexed by normalized domain keys.
     */
    private final Map<String, Crawler> crawlersByDomain = new ConcurrentHashMap<>();

    @Override
    public Optional<Crawler> getCrawlerForDomain(String domain) {
        if (domain == null) return Optional.empty();
        return Optional.ofNullable(crawlersByDomain.get(normalizeDomain(domain)));
    }

    @Override
    public void registerCrawler(String domain, Crawler crawler) {
        if (domain == null || crawler == null) {
            throw new IllegalArgumentException("Domain and crawler must not be null");
        }
        String normalized = normalizeDomain(domain);
        crawlersByDomain.put(normalized, crawler);
        LOGGER.info("[REGISTRY] Registered crawler for domain: {}", normalized);
    }

    /**
     * Normalizes a domain string by trimming whitespace and converting to lower case.
     *
     * @param domain the raw domain string
     * @return normalized domain string suitable for consistent lookup
     */
    private String normalizeDomain(String domain) {
        return domain.trim().toLowerCase(Locale.ROOT);
    }
}
