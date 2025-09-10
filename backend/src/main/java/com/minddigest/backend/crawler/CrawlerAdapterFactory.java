package com.minddigest.backend.crawler;

import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerAdapterBuilder;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import com.minddigest.backend.crawler.interfaces.UsesCrawlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Factory responsible for instantiating {@link Crawler} adapters for given {@link CrawlerPageProcessor}s.
 * <p>
 * This factory manages a registry of adapter builders keyed by qualifiers, which are linked to processors
 * via the {@link UsesCrawlerAdapter} annotation.
 * </p>
 * <p>
 * Adapter builders and processors are injected by Spring and registered during construction.
 * When requested, the factory creates a properly configured {@link Crawler} instance for a processor,
 * supporting configurable thread count.
 * </p>
 */
@Component
public class CrawlerAdapterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerAdapterFactory.class);

    private final Map<String, CrawlerAdapterBuilder> adapterBuilders;

    // Mapping from processor class to adapter qualifier, injected via @UsesCrawlerAdapter annotation
    private final Map<Class<?>, String> processorToAdapterQualifier = new ConcurrentHashMap<>();

    /**
     * Constructs a new {@code CrawlerAdapterFactory} with the provided adapter builders and processors.
     * <p>
     * During initialization, it scans all provided processors, extracts the adapter qualifier
     * from their {@link UsesCrawlerAdapter} annotation, and verifies corresponding adapter builders exist.
     * </p>
     *
     * @param adapterBuilders map of adapter builder beans by their Spring qualifier
     * @param processors      all registered {@link CrawlerPageProcessor} beans
     * @throws IllegalStateException if any processor is missing the {@link UsesCrawlerAdapter} annotation
     *                               or references a non-existent adapter builder qualifier
     */
    public CrawlerAdapterFactory(Map<String, CrawlerAdapterBuilder> adapterBuilders,
                                 List<CrawlerPageProcessor> processors) {
        this.adapterBuilders = adapterBuilders;
        initRegistry(processors);
    }

    /**
     * Initializes the internal processor-to-adapter mapping registry based on the {@link UsesCrawlerAdapter}
     * annotation found on each processor class.
     * <p>
     * This method ensures all referenced adapter builder qualifiers exist within the injected adapter builders.
     * </p>
     *
     * @param processors list of processors to register
     * @throws IllegalStateException if a processor is missing the annotation or references a missing adapter builder
     */
    private void initRegistry(List<CrawlerPageProcessor> processors) {
        for (CrawlerPageProcessor processor : processors) {
            Class<? extends CrawlerPageProcessor> processorClass = processor.getClass();

            UsesCrawlerAdapter annotation = AnnotationUtils.findAnnotation(processorClass, UsesCrawlerAdapter.class);
            if (annotation == null) {
                throw new IllegalStateException("Processor class '" + processorClass.getName()
                        + "' is missing required @UsesCrawlerAdapter annotation.");
            }

            String qualifier = annotation.value().trim();
            if (!adapterBuilders.containsKey(qualifier)) {
                throw new IllegalStateException("No CrawlerAdapterBuilder bean found for qualifier '" + qualifier
                        + "' required by processor: " + processorClass.getName());
            }

            processorToAdapterQualifier.put(processorClass, qualifier);
            LOGGER.debug("[ADAPTER-FACTORY] Registered processor '{}' -> adapter '{}'",
                    processorClass.getSimpleName(), qualifier);
        }

        LOGGER.info("[ADAPTER-FACTORY] Registered {} processor to adapter mappings", processorToAdapterQualifier.size());
    }

    /**
     * Creates a {@link Crawler} adapter instance for the specified processor using the corresponding
     * {@link CrawlerAdapterBuilder}.
     * <p>
     * The created adapter is configured with the provided thread count for concurrent crawling.
     * </p>
     *
     * @param processor   the processor to create an adapter for
     * @param threadCount number of threads the adapter should use for crawling
     * @return a new {@link Crawler} instance configured with the specified thread count
     * @throws IllegalArgumentException if no adapter mapping is registered for the processor type
     * @throws IllegalStateException    if the adapter builder for the mapped qualifier is missing
     */
    public Crawler createAdapter(CrawlerPageProcessor processor, int threadCount) {
        Class<? extends CrawlerPageProcessor> processorClass = processor.getClass();
        String qualifier = processorToAdapterQualifier.get(processorClass);

        if (qualifier == null) {
            throw new IllegalArgumentException("No adapter mapping registered for processor class: "
                    + processorClass.getName());
        }

        CrawlerAdapterBuilder builder = adapterBuilders.get(qualifier);
        if (builder == null) {
            throw new IllegalStateException("Adapter builder for qualifier '" + qualifier + "' not found in registry.");
        }

        LOGGER.debug("[ADAPTER-FACTORY] Creating adapter '{}' for processor '{}'",
                qualifier, processorClass.getSimpleName());

        return builder.build(processor, threadCount);
    }
}
