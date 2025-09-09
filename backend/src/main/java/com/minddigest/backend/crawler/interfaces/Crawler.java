package com.minddigest.backend.crawler.interfaces;

import com.minddigest.backend.dto.DigestEntryDto;

import java.util.List;

/**
 * Defines the contract for a web crawler implementation.
 * <p>
 * A Crawler is responsible for initializing with a given domain and starting URL,
 * then executing the crawling process to collect and return crawled data entries.
 * </p>
 */
public interface Crawler {

    /**
     * Initializes the crawler with the target domain and the initial URL to start crawling from.
     * This method should prepare any necessary state before crawling begins.
     *
     * @param domain   the domain name of the website to crawl (e.g., "spektrum.de")
     * @param startUrl the URL from which the crawling should start
     */
    void init(String domain, String startUrl);

    /**
     * Executes the crawling process.
     * This method should perform the crawling according to the initialized parameters
     * and return the results collected during the crawl.
     *
     * @return a list of {@link DigestEntryDto} representing the crawled entries
     */
    List<DigestEntryDto> crawl();
}
