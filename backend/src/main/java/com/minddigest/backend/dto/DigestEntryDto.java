package com.minddigest.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * Data Transfer Object representing a digest entry extracted via crawling or user input.
 * <p>
 * Used for transferring data between layers such as controller and service.
 * Contains metadata like title, summary, author, and source URL.
 * </p>
 */
public class DigestEntryDto {


    /**
     * Title of the content (e.g., article, blog post).
     * This field is mandatory and has a maximum length of 256 characters.
     */
    @NotBlank(message = "Title must not be blank")
    @Size(max = 256, message = "Title must be at most 256 characters long")
    private String title;

    /**
     * Short summary or description of the content.
     * This field is optional but limited to 5000 characters in length.
     */
    @Size(max = 5000, message = "Summary must be at most 5000 characters long")
    private String summary;

    /**
     * Name of the author or source if available.
     * Optional field with a maximum length of 256 characters.
     */
    @Size(max = 256, message = "Author must be at most 256 characters long")
    private String author;

    /**
     * Original URL of the source from which the entry was extracted.
     * This field is mandatory and must not exceed 1000 characters.
     */
    @NotNull(message = "Source URL must not be null")
    @Size(max = 1000, message = "Source URL must be at most 1000 characters long")
    private String sourceUrl;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
