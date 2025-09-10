package com.minddigest.backend.crawler.interfaces;

import com.minddigest.backend.dto.DigestEntryDto;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;


/**
 * Base interface for all crawler-specific page processors.
 * <p>
 * Extends WebMagic's {@link PageProcessor} interface and adds
 * initialization and result retrieval methods tailored for the application's crawling logic.
 * </p>
 */
public interface CrawlerPageProcessor extends PageProcessor {

    /**
     * Initializes the crawler with runtime parameters such as domain and start URL.
     * This method is typically called before starting the crawl to configure
     * the processor with context-specific data.
     *
     * @param domain   the domain name this crawler should target (e.g., "spektrum.de")
     * @param startUrl the initial URL from which the crawling should start
     */
    void init(String domain, String startUrl);

    /**
     * Returns the list of results collected during crawling.
     * Typically, this includes extracted data such as article summaries or entries.
     *
     * @return a list of {@link DigestEntryDto} containing the crawl results
     */
    List<DigestEntryDto> getResults();
}
