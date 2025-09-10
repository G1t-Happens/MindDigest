package com.minddigest.backend.crawler;

import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerAdapterBuilder;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import com.minddigest.backend.crawler.interfaces.UsesCrawlerBuilder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Factory class responsible for creating {@link Crawler} adapter instances
 * based on the {@link CrawlerPageProcessor} implementations.
 * <p>
 * This factory automatically registers adapters by scanning the
 * {@link UsesCrawlerBuilder} annotation on processor classes.
 * It then uses reflection to instantiate the appropriate adapter
 * with the configured thread count.
 * </p>
 */
@Component
public class CrawlerAdapterFactory {

    private final Map<Class<?>, CrawlerAdapterBuilder> builderMap;

    private final Map<Class<?>, CrawlerAdapterBuilder> adapterRegistry = new ConcurrentHashMap<>();

    public CrawlerAdapterFactory(
            List<CrawlerAdapterBuilder> builders,
            List<CrawlerPageProcessor> processors
    ) {

        this.builderMap = builders.stream()
                .collect(Collectors.toMap(
                        CrawlerAdapterBuilder::getClass,
                        Function.identity()
                ));

        initRegistry(processors);
    }

    private void initRegistry(List<CrawlerPageProcessor> processors) {
        for (CrawlerPageProcessor processor : processors) {
            Class<?> processorClass = processor.getClass();

            UsesCrawlerBuilder annotation = AnnotationUtils.findAnnotation(processorClass, UsesCrawlerBuilder.class);
            if (annotation == null) {
                throw new IllegalStateException("Missing @UsesCrawlerBuilder on " + processorClass);
            }

            CrawlerAdapterBuilder builder = builderMap.get(annotation.value());
            if (builder == null) {
                throw new IllegalStateException("No bean found for builder class " + annotation.value().getName());
            }

            adapterRegistry.put(processorClass, builder);
        }
    }

    public Crawler createAdapter(CrawlerPageProcessor processor) {
        CrawlerAdapterBuilder builder = adapterRegistry.get(processor.getClass());
        if (builder == null) {
            throw new IllegalArgumentException("No adapter registered for " + processor.getClass());
        }
        return builder.build(processor);
    }
}
