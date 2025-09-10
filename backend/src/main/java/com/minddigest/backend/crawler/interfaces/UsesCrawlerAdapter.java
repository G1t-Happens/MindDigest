package com.minddigest.backend.crawler.interfaces;

import java.lang.annotation.*;

/**
 * Annotation to specify which {@link Crawler} adapter implementation
 * should be used with a given crawler page processor.
 * <p>
 * This annotation allows dynamic binding between a {@link CrawlerPageProcessor}
 * and its corresponding {@link Crawler} adapter class.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 * &#64;UsesCrawlerAdapter(WebMagicCrawlerAdapter.class)
 * public class MyPageProcessor implements CrawlerPageProcessor {
 *     // ...
 * }
 * </pre>
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UsesCrawlerAdapter {

    /**
     * The {@link Crawler} implementation class that should be used
     * as an adapter for the annotated crawler page processor.
     *
     * @return the class of the crawler adapter
     */
    Class<? extends Crawler> value();
}
