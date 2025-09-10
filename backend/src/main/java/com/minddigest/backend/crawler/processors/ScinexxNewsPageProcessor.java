package com.minddigest.backend.crawler.processors;

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
 * PageProcessor implementation for crawling news articles from scinexx.de.
 * <p>
 * This processor identifies article URLs matching a specific pattern,
 * filters out premium articles, extracts relevant data such as title, content,
 * and author, and collects the results as {@link DigestEntryDto} instances.
 * </p>
 */
@CrawlerComponent(domain = "scinexx.de")
public class ScinexxNewsPageProcessor implements CrawlerPageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScinexxNewsPageProcessor.class);

    /**
     * Stores the list of extracted articles as {@link DigestEntryDto}.
     */
    private final List<DigestEntryDto> results = new ArrayList<>();

    /**
     * WebMagic {@link Site} configuration with retries, timeouts, charset, and user-agent.
     */
    private final Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setTimeOut(10000)
            .setCharset("UTF-8")
            .setUserAgent("Mozilla/5.0 (compatible; MindDigestBot/1.0)");

    /**
     * Regex pattern to identify valid article URLs for the target domain.
     */
    private Pattern articleUrlPattern;

    // XPath expressions for locating HTML elements within the page
    private static final String XPATH_PREMIUM_ARTICLE = "todo";
    private static final String XPATH_TITLE = "todo";
    private static final String XPATH_PARAGRAPHS = "todo";
    private static final String XPATH_AUTHOR_MAIN = "todo";
    private static final String XPATH_AUTHOR_FALLBACK = "todo";

    /**
     * Initializes the processor for a given domain and start URL.
     * <p>
     * Compiles the regex pattern for article URLs based on the domain.
     * Clears previous crawl results.
     * </p>
     *
     * @param domain   the target domain (e.g., "scinexx.de")
     * @param startUrl the initial URL to start crawling from
     */
    @Override
    public void init(String domain, String startUrl) {
        this.articleUrlPattern = Pattern.compile("https://www\\." + Pattern.quote(domain) + "/news/.+?/\\d+");
        this.results.clear();
        LOGGER.info("ScinexxNewsPageProcessor initialized for domain: {} with startUrl: {}", domain, startUrl);
    }

    /**
     * Processes a single web page:
     * <ul>
     *   <li>If the URL is not an article, extracts and queues all matching article links.</li>
     *   <li>If the article is premium, skips it.</li>
     *   <li>Otherwise, extracts the title, content paragraphs, and author information.</li>
     *   <li>Adds valid articles as {@link DigestEntryDto} to the results list.</li>
     * </ul>
     *
     * @param page the {@link Page} to process
     */
    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();

        // If the URL is not a target article, queue all matching links found on the page
        if (!articleUrlPattern.matcher(url).matches()) {
            List<String> links = page.getHtml().links()
                    .regex(articleUrlPattern.pattern())
                    .all();
            page.addTargetRequests(links);
            LOGGER.debug("Added {} links to target requests", links.size());
            return;
        }

        // Skip premium articles
        boolean isPremium = page.getHtml()
                .xpath(XPATH_PREMIUM_ARTICLE)
                .match();

        if (isPremium) {
            LOGGER.debug("Skipping premium article: {}", url);
            page.setSkip(true);
            return;
        }

        // Extract article details
        String title = page.getHtml().xpath(XPATH_TITLE).toString();
        List<String> paragraphs = page.getHtml().xpath(XPATH_PARAGRAPHS).all();
        String content = String.join("\n", paragraphs).trim();

        // Try main author extraction, fallback if empty
        String author = page.getHtml().xpath(XPATH_AUTHOR_MAIN).toString();
        if (!StringUtils.hasText(author)) {
            author = page.getHtml().xpath(XPATH_AUTHOR_FALLBACK).toString();
        }

        // Skip if mandatory fields missing
        if (!StringUtils.hasText(title) || content.isEmpty()) {
            LOGGER.warn("Skipping page due to missing title or content: {}", url);
            page.setSkip(true);
            return;
        }

        // Build and store result DTO
        DigestEntryDto entry = new DigestEntryDto();
        entry.setTitle(title);
        entry.setSummary(content);
        entry.setSourceUrl(url);
        entry.setAuthor(author != null ? author : "");

        results.add(entry);
        LOGGER.debug("Added article: {}", title);
    }

    /**
     * Returns the configured {@link Site} for crawling.
     *
     * @return the configured Site instance
     */
    @Override
    public Site getSite() {
        return site;
    }

    /**
     * Returns the list of extracted {@link DigestEntryDto} results.
     *
     * @return list of collected digest entries
     */
    @Override
    public List<DigestEntryDto> getResults() {
        return results;
    }
}

