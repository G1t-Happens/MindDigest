package com.minddigest.backend.crawler.interfaces;

import java.util.Optional;


/**
 * Registry interface for managing crawler instances mapped to specific domains.
 * <p>
 * Provides methods to register new crawlers and retrieve existing ones by domain name.
 * This enables dynamic lookup and management of crawler implementations based on target domains.
 * </p>
 */
public interface CrawlerRegistry {

    /**
     * Retrieves the crawler associated with the specified domain.
     *
     * @param domain the domain name for which to find a crawler (e.g., "spektrum.de")
     * @return an {@link Optional} containing the crawler if found, or empty if no crawler
     * is registered for the given domain
     */
    Optional<Crawler> getCrawlerForDomain(String domain);

    /**
     * Registers a crawler instance for the given domain.
     * <p>
     * If a crawler is already registered for the domain, it may be overwritten.
     * </p>
     *
     * @param domain  the domain name to associate with the crawler
     * @param crawler the crawler instance to register
     */
    void registerCrawler(String domain, Crawler crawler);
}
