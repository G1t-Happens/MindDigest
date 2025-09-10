package com.minddigest.backend.crawler.processors;

import com.minddigest.backend.crawler.adapters.webmagic.WebMagicCrawlerAdapter;
import com.minddigest.backend.crawler.interfaces.UsesCrawlerAdapter;
import com.minddigest.backend.dto.DigestEntryDto;
import com.minddigest.backend.crawler.interfaces.CrawlerComponent;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * {@link CrawlerPageProcessor} implementation for crawling news articles from <code>spektrum.de</code>.
 * <p>
 * This processor is designed to identify article URLs matching a specific regex pattern,
 * skip premium content identified by a CSS class, extract key article data such as title,
 * content paragraphs, and author, and collect these as {@link DigestEntryDto} instances.
 * </p>
 * <p>
 * The processor is annotated with {@link CrawlerComponent} for domain binding,
 * and {@link UsesCrawlerAdapter} to specify usage of {@link WebMagicCrawlerAdapter}.
 * </p>
 */
@CrawlerComponent(domain = "spektrum.de")
@UsesCrawlerAdapter("webMagicAdapter")
public class SpektrumNewsPageProcessor implements CrawlerPageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpektrumNewsPageProcessor.class);

    /**
     * Holds the list of extracted article data as {@link DigestEntryDto}.
     */
    private final List<DigestEntryDto> results = new ArrayList<>();

    /**
     * WebMagic {@link Site} configuration including retry settings, timeout, charset, and user-agent string.
     */
    private final Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setTimeOut(10000)
            .setCharset("UTF-8")
            .setUserAgent("Mozilla/5.0 (compatible; MindDigestBot/1.0)");

    /**
     * Regex pattern to recognize valid article URLs within the spektrum.de domain.
     */
    private Pattern articleUrlPattern;

    // XPath expressions for parsing different elements within the HTML pages
    private static final String XPATH_PREMIUM_ARTICLE = "//article[contains(@class, 'pw-premium')]";
    private static final String XPATH_TITLE = "//span[@class='content__title']/text()";
    private static final String XPATH_PARAGRAPHS = "//article[contains(@class, 'content') and contains(@class, 'pw-free')]//p//text()";
    private static final String XPATH_AUTHOR_MAIN = "//div[contains(@class, 'content__author__info__name')]//a[@class='line']/text()";
    private static final String XPATH_AUTHOR_FALLBACK = "//div[contains(@class, 'content__copyright')]//span/text()";

    /**
     * Initializes the processor for the specified domain and start URL.
     * <p>
     * This method compiles a regex pattern for detecting article URLs based on the domain,
     * and clears any previously collected results to prepare for a fresh crawl.
     * </p>
     *
     * @param domain   the domain this processor is associated with (e.g., "spektrum.de")
     * @param startUrl the initial URL to begin crawling from
     */
    @Override
    public void init(String domain, String startUrl) {
        this.articleUrlPattern = Pattern.compile("https://www\\." + Pattern.quote(domain) + "/news/.+?/\\d+");
        this.results.clear();
        LOGGER.info("SpektrumNewsPageProcessor initialized for domain: {} with startUrl: {}", domain, startUrl);
    }

    /**
     * Processes an individual {@link Page} during crawling.
     * <ul>
     *   <li>If the current URL does not match the article pattern, extracts all matching article links and queues them for crawling.</li>
     *   <li>Detects and skips premium articles by checking the configured XPath.</li>
     *   <li>Extracts title, content paragraphs, and author information from valid articles.</li>
     *   <li>Skips pages missing mandatory content.</li>
     *   <li>Creates and stores {@link DigestEntryDto} objects for successfully extracted articles.</li>
     * </ul>
     *
     * @param page the web page to process
     */
    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();

        // If current URL does not match article pattern, queue all matching links on the page
        if (!articleUrlPattern.matcher(url).matches()) {
            List<String> links = page.getHtml().links()
                    .regex(articleUrlPattern.pattern())
                    .all();
            page.addTargetRequests(links);
            LOGGER.debug("Added {} links to target requests", links.size());
            return;
        }

        // Check for premium articles and skip them
        boolean isPremium = page.getHtml()
                .xpath(XPATH_PREMIUM_ARTICLE)
                .match();

        if (isPremium) {
            LOGGER.debug("Skipping premium article: {}", url);
            // Mark this page to be skipped from further processing and storage, effectively excluding it from the crawl results.
            page.setSkip(true);
            return;
        }

        // Extract article title, paragraphs, and author information
        String title = page.getHtml().xpath(XPATH_TITLE).toString();
        List<String> paragraphs = page.getHtml().xpath(XPATH_PARAGRAPHS).all();
        String content = String.join("\n", paragraphs).trim();

        // Extract author with fallback
        String author = page.getHtml().xpath(XPATH_AUTHOR_MAIN).toString();
        if (!StringUtils.hasText(author)) {
            author = page.getHtml().xpath(XPATH_AUTHOR_FALLBACK).toString();
        }

        // Validate mandatory fields
        if (!StringUtils.hasText(title) || content.isEmpty()) {
            LOGGER.warn("Skipping page due to missing title or content: {}", url);
            // Mark this page to be skipped from further processing and storage, effectively excluding it from the crawl results.
            page.setSkip(true);
            return;
        }

        // Build the result object and add to results list
        DigestEntryDto entry = new DigestEntryDto();
        entry.setTitle(title);
        entry.setSummary(content);
        entry.setSourceUrl(url);
        entry.setAuthor(author != null ? author : "");

        results.add(entry);
        LOGGER.debug("Added article: {}", title);
    }

    /**
     * Returns the configured {@link Site} instance used for crawling.
     *
     * @return the {@link Site} configuration
     */
    @Override
    public Site getSite() {
        return site;
    }

    /**
     * Retrieves the list of extracted {@link DigestEntryDto} articles.
     *
     * @return a list of collected digest entries
     */
    @Override
    public List<DigestEntryDto> getResults() {
        return results;
    }
}
