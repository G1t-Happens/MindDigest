package com.minddigest.backend.crawler.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to specify which crawler adapter should be used for a given {@link com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor}.
 * <p>
 * This annotation binds a processor implementation to a named crawler adapter,
 * identified by the adapter's qualifier string.
 * </p>
 * <p>
 * The value typically corresponds to the Spring qualifier of a {@link com.minddigest.backend.crawler.interfaces.CrawlerAdapterBuilder}
 * that will be used to create the {@link com.minddigest.backend.crawler.interfaces.Crawler} instance.
 * </p>
 * <p>
 * Used by the {@link com.minddigest.backend.crawler.CrawlerAdapterFactory} during adapter creation.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UsesCrawlerAdapter {

    /**
     * Qualifier name of the crawler adapter to be used for the annotated processor.
     *
     * @return the adapter qualifier string
     */
    String value();
}

