package com.minddigest.backend.crawler.processors;

import com.minddigest.backend.crawler.WebMagicAdapterBuilder;
import com.minddigest.backend.crawler.WebMagicCrawlerAdapter;
import com.minddigest.backend.crawler.interfaces.UsesCrawlerBuilder;
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
 * {@link CrawlerPageProcessor} implementation for crawling news articles from <code>scinexx.de</code>.
 * <p>
 * This processor is responsible for identifying valid article URLs matching a defined pattern,
 * filtering out premium content, extracting relevant fields such as title, content paragraphs, and author,
 * and collecting the extracted data as {@link DigestEntryDto} instances.
 * </p>
 * <p>
 * The class is annotated with {@link CrawlerComponent} to bind it to the <code>scinexx.de</code> domain,
 * and with {@link UsesCrawlerBuilder} to specify {@link WebMagicCrawlerAdapter} as the crawler adapter.
 * </p>
 */
@CrawlerComponent(domain = "scinexx.de")
@UsesCrawlerBuilder(WebMagicAdapterBuilder.class)
public class ScinexxNewsPageProcessor implements CrawlerPageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScinexxNewsPageProcessor.class);

    /**
     * List that stores all extracted articles as {@link DigestEntryDto} objects.
     */
    private final List<DigestEntryDto> results = new ArrayList<>();

    /**
     * Configuration for the web crawling site including retry policy, timeouts, charset, and user-agent.
     */
    private final Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setTimeOut(10000)
            .setCharset("UTF-8")
            .setUserAgent("Mozilla/5.0 (compatible; MindDigestBot/1.0)");

    /**
     * Compiled regex pattern used to identify valid article URLs within the target domain.
     */
    private Pattern articleUrlPattern;

    // TODO: Define actual XPath expressions for locating HTML elements inside the crawled pages.
    private static final String XPATH_PREMIUM_ARTICLE = "todo";
    private static final String XPATH_TITLE = "todo";
    private static final String XPATH_PARAGRAPHS = "todo";
    private static final String XPATH_AUTHOR_MAIN = "todo";
    private static final String XPATH_AUTHOR_FALLBACK = "todo";

    /**
     * Initializes the page processor for the specified domain and start URL.
     * <p>
     * This method compiles the article URL pattern based on the domain and clears any previously
     * stored results to prepare for a new crawl session.
     * </p>
     *
     * @param domain   the domain this processor will crawl, e.g. <code>scinexx.de</code>
     * @param startUrl the initial URL from which crawling begins
     */
    @Override
    public void init(String domain, String startUrl) {
        this.articleUrlPattern = Pattern.compile("https://www\\." + Pattern.quote(domain) + "/news/.+?/\\d+");
        this.results.clear();
        LOGGER.info("ScinexxNewsPageProcessor initialized for domain: {} with startUrl: {}", domain, startUrl);
    }

    /**
     * Processes a single crawled {@link Page}.
     * <ul>
     *     <li>If the URL does not match an article pattern, all matching links are extracted and queued for crawling.</li>
     *     <li>Premium articles are detected via XPath and skipped.</li>
     *     <li>For valid articles, the title, content paragraphs, and author are extracted.</li>
     *     <li>Articles with missing mandatory fields are skipped.</li>
     *     <li>Successfully extracted articles are converted into {@link DigestEntryDto} objects and stored.</li>
     * </ul>
     *
     * @param page the {@link Page} object representing the crawled web page to process
     */
    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();

        // Queue article links if the URL is not itself a target article
        if (!articleUrlPattern.matcher(url).matches()) {
            List<String> links = page.getHtml().links()
                    .regex(articleUrlPattern.pattern())
                    .all();
            page.addTargetRequests(links);
            LOGGER.debug("Added {} links to target requests", links.size());
            return;
        }

        // Detect and skip premium articles
        boolean isPremium = page.getHtml()
                .xpath(XPATH_PREMIUM_ARTICLE)
                .match();

        if (isPremium) {
            LOGGER.debug("Skipping premium article: {}", url);
            page.setSkip(true);
            return;
        }

        // Extract article data
        String title = page.getHtml().xpath(XPATH_TITLE).toString();
        List<String> paragraphs = page.getHtml().xpath(XPATH_PARAGRAPHS).all();
        String content = String.join("\n", paragraphs).trim();

        // Attempt to extract author information, fallback if primary XPath is empty
        String author = page.getHtml().xpath(XPATH_AUTHOR_MAIN).toString();
        if (!StringUtils.hasText(author)) {
            author = page.getHtml().xpath(XPATH_AUTHOR_FALLBACK).toString();
        }

        // Skip if essential data is missing
        if (!StringUtils.hasText(title) || content.isEmpty()) {
            LOGGER.warn("Skipping page due to missing title or content: {}", url);
            page.setSkip(true);
            return;
        }

        // Build and store the digest entry
        DigestEntryDto entry = new DigestEntryDto();
        entry.setTitle(title);
        entry.setSummary(content);
        entry.setSourceUrl(url);
        entry.setAuthor(author != null ? author : "");

        results.add(entry);
        LOGGER.debug("Added article: {}", title);
    }

    /**
     * Returns the configured {@link Site} instance for this processor.
     *
     * @return the configured {@link Site} used during crawling
     */
    @Override
    public Site getSite() {
        return site;
    }

    /**
     * Retrieves the list of extracted article results.
     *
     * @return a list of {@link DigestEntryDto} representing the extracted articles
     */
    @Override
    public List<DigestEntryDto> getResults() {
        return results;
    }
}
