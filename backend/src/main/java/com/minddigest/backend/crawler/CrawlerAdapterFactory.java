package com.minddigest.backend.crawler;

import com.minddigest.backend.config.CrawlerProperties;
import com.minddigest.backend.crawler.interfaces.Crawler;
import com.minddigest.backend.crawler.interfaces.CrawlerPageProcessor;
import com.minddigest.backend.crawler.interfaces.UsesCrawlerAdapter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


/**
 * Factory class responsible for creating {@link Crawler} adapter instances
 * based on the {@link CrawlerPageProcessor} implementations.
 * <p>
 * This factory automatically registers adapters by scanning the
 * {@link UsesCrawlerAdapter} annotation on processor classes.
 * It then uses reflection to instantiate the appropriate adapter
 * with the configured thread count.
 * </p>
 */
@Component
public class CrawlerAdapterFactory {

    private final CrawlerProperties crawlerProperties;

    /**
     * Registry mapping processor classes to adapter creation functions.
     * This enables dynamic adapter instantiation without hardcoded conditionals.
     */
    private final Map<Class<?>, Function<CrawlerPageProcessor, Crawler>> adapterRegistry = new ConcurrentHashMap<>();

    /**
     * Constructs the factory, initializing the adapter registry based on
     * the provided list of {@link CrawlerPageProcessor} implementations.
     *
     * @param crawlerProperties configuration properties for crawler settings
     * @param processors        list of available page processors to register
     * @throws IllegalStateException if a processor lacks the required {@link UsesCrawlerAdapter} annotation
     */
    public CrawlerAdapterFactory(CrawlerProperties crawlerProperties, List<CrawlerPageProcessor> processors) {
        this.crawlerProperties = crawlerProperties;
        initRegistry(processors);
    }

    /**
     * Initializes the internal adapter registry by inspecting each processor's
     * {@link UsesCrawlerAdapter} annotation, registering the corresponding adapter
     * creation function keyed by the processor's class.
     *
     * @param processors list of page processors to register
     * @throws IllegalStateException if a processor is missing the {@link UsesCrawlerAdapter} annotation
     */
    private void initRegistry(List<CrawlerPageProcessor> processors) {
        for (CrawlerPageProcessor processor : processors) {
            Class<?> processorClass = processor.getClass();
            UsesCrawlerAdapter annotation = AnnotationUtils.findAnnotation(processorClass, UsesCrawlerAdapter.class);
            if (annotation == null) {
                throw new IllegalStateException("CrawlerPageProcessor " + processorClass + " missing @UsesCrawlerAdapter");
            }
            Class<? extends Crawler> adapterClass = annotation.value();
            adapterRegistry.put(processorClass, p -> createAdapterInstance(adapterClass, p));
        }
    }

    /**
     * Instantiates a crawler adapter of the specified type using reflection.
     * The adapter constructor is expected to accept a {@link CrawlerPageProcessor}
     * and an integer thread count.
     *
     * @param adapterClass the adapter class to instantiate
     * @param processor    the processor instance to pass to the adapter constructor
     * @return a new instance of the adapter
     * @throws RuntimeException if instantiation fails due to reflection issues
     */
    private Crawler createAdapterInstance(Class<? extends Crawler> adapterClass, CrawlerPageProcessor processor) {
        try {
            return adapterClass.getConstructor(CrawlerPageProcessor.class, int.class)
                    .newInstance(processor, crawlerProperties.getThreads());
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate adapter " + adapterClass.getName(), e);
        }
    }

    /**
     * Creates a crawler adapter instance for the specified processor by
     * looking up the registered adapter creator function.
     *
     * @param processor the processor for which to create an adapter
     * @return a {@link Crawler} instance wrapping the processor
     * @throws IllegalArgumentException if no adapter is registered for the processor's class
     */
    public Crawler createAdapter(CrawlerPageProcessor processor) {
        Function<CrawlerPageProcessor, Crawler> creator = adapterRegistry.get(processor.getClass());
        if (creator == null) {
            throw new IllegalArgumentException("No adapter registered for processor type: " + processor.getClass());
        }
        return creator.apply(processor);
    }
}
