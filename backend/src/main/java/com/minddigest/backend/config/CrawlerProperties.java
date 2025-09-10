package com.minddigest.backend.config;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "crawler")
@Validated
public class CrawlerProperties {

    @NotEmpty
    private List<@Valid Site> sites;

    @Min(1)
    private Integer threads = 4;

    public List<Site> getSites() { return sites; }
    public void setSites(List<Site> sites) { this.sites = sites; }

    public Integer getThreads() { return threads; }
    public void setThreads(Integer threads) { this.threads = threads; }

    public static class Site {
        @NotBlank
        private String name;

        @NotBlank
        private String domain;

        @NotBlank
        @URL
        private String startUrl;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }

        public String getStartUrl() { return startUrl; }
        public void setStartUrl(String startUrl) { this.startUrl = startUrl; }
    }
}
