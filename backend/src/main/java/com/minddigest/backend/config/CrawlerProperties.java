package com.minddigest.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration properties class that maps web crawler related settings
 * from {@code application.yml} or {@code application.properties} files
 * with prefix {@code "crawler"}.
 * <p>
 * This class provides structured access to the list of target websites
 * to be crawled, including their metadata such as domain and start URL,
 * as well as global crawler settings like thread count.
 * </p>
 *
 * <p>Example YAML configuration:</p>
 * <pre>{@code
 * crawler:
 *   threads: 4
 *   sites:
 *     - name: spektrum
 *       domain: spektrum.de
 *       startUrl: https://www.spektrum.de/news
 * }</pre>
 */
@Configuration
@ConfigurationProperties(prefix = "crawler")
public class CrawlerProperties {

    /**
     * List of crawl targets representing websites to be processed by the crawler.
     * Each site contains its own configuration such as domain and start URL.
     */
    private List<Site> sites;

    /**
     * Gets the list of configured crawl sites.
     *
     * @return the list of crawl sites
     */
    public List<Site> getSites() {
        return sites;
    }

    /**
     * Sets the list of crawl sites.
     *
     * @param sites list of sites to crawl
     */
    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    /**
     * Number of threads to be used for parallel crawling operations.
     * This allows controlling concurrency at the application level.
     */
    private Integer threads = null;

    /**
     * Gets the configured number of threads for crawling.
     *
     * @return number of threads or {@code null} if not configured
     */
    public Integer getThreads() {
        return threads;
    }

    /**
     * Sets the number of threads to be used for crawling.
     *
     * @param threads number of threads
     */
    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    /**
     * Represents the configuration for a single crawlable website.
     */
    public static class Site {

        /**
         * Logical name or identifier of the site.
         * Useful for logging, monitoring, or mapping to specific crawler implementations.
         */
        private String name;

        /**
         * Domain name of the website, used to associate the site with a crawler.
         * Example: {@code "spektrum.de"}
         */
        private String domain;

        /**
         * Starting URL for the crawl process on this site.
         * Usually a landing page, category page or news feed URL.
         */
        private String startUrl;

        /**
         * Gets the logical name of the site.
         *
         * @return the site name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the logical name of the site.
         *
         * @param name the site name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the domain of the site.
         *
         * @return the site domain
         */
        public String getDomain() {
            return domain;
        }

        /**
         * Sets the domain of the site.
         *
         * @param domain the site domain
         */
        public void setDomain(String domain) {
            this.domain = domain;
        }

        /**
         * Gets the starting URL for crawling this site.
         *
         * @return the start URL
         */
        public String getStartUrl() {
            return startUrl;
        }

        /**
         * Sets the starting URL for crawling this site.
         *
         * @param startUrl the start URL
         */
        public void setStartUrl(String startUrl) {
            this.startUrl = startUrl;
        }
    }
}
