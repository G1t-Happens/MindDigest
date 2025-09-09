package com.minddigest.backend.crawler.interfaces;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Annotation to mark a class as a crawler page processor for a specific domain.
 * <p>
 * Classes annotated with {@code @CrawlerComponent} are automatically detected as Spring components,
 * allowing them to be managed by the Spring container and discovered via component scanning.
 * </p>
 * <p>
 * The {@code domain} attribute specifies the target domain this crawler implementation handles.
 * This information can be used for routing requests to the appropriate crawler based on domain.
 * </p>
 *
 * <pre>
 * Example usage:
 * &#64;CrawlerComponent(domain = "spektrum.de")
 * public class SpektrumNewsPageProcessor implements CrawlerPageProcessor {
 *     // implementation details...
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface CrawlerComponent {

    /**
     * The domain name this crawler component supports.
     * Example: "spektrum.de"
     *
     * @return the target domain for the crawler component
     */
    String domain();
}
