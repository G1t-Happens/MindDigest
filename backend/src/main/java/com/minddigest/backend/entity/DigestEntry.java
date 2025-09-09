package com.minddigest.backend.entity;

import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serial;
import java.util.Objects;

/**
 * Entity representing a digest entry stored in the database.
 * <p>
 * Extends {@link AbstractEntity} to include auditing fields such as creation and update timestamps.
 * Contains fields for title, summary, author, and source URL of the digest entry.
 * </p>
 */
@Entity
@Table(name = "digest_entries")
public class DigestEntry extends AbstractEntity {

    @Serial
    private static final long serialVersionUID = -9085807652232456083L;

    /**
     * Primary key identifier for the digest entry.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    /**
     * Title of the digest entry.
     * Maximum length limited to 256 characters.
     */
    @Column(length = 256)
    private String title;

    /**
     * Summary or description of the digest entry.
     * Maximum length limited to 5000 characters.
     */
    @Column(length = 5000)
    private String summary;

    /**
     * Name of the author of the digest entry.
     * Maximum length limited to 256 characters.
     */
    @Column(length = 256)
    private String author;

    /**
     * Source URL from which this digest entry was extracted.
     * Maximum length limited to 1000 characters.
     */
    @Column(length = 1000)
    private String sourceUrl;

    /**
     * Returns the unique identifier of this digest entry.
     *
     * @return the database-generated ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this digest entry.
     * Generally managed by JPA provider and should not be set manually.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the title of the digest entry.
     *
     * @return the title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the digest entry.
     *
     * @param title the title string to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the summary or description of the digest entry.
     *
     * @return the summary string
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Sets the summary or description of the digest entry.
     *
     * @param summary the summary string to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Returns the author name of the digest entry.
     *
     * @return the author string
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author name of the digest entry.
     *
     * @param author the author string to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the source URL of the digest entry.
     *
     * @return the source URL string
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * Sets the source URL of the digest entry.
     *
     * @param sourceUrl the source URL string to set
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    /**
     * Overrides equality to compare based on entity identifier.
     * <p>
     * Properly handles Hibernate proxy objects by comparing their persistent classes.
     * Two entities are equal if their IDs are non-null and equal.
     * </p>
     *
     * @param o the object to compare
     * @return true if the objects represent the same entity, false otherwise
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Class<?> oEffectiveClass = (o instanceof HibernateProxy hProxy)
                ? hProxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = (this instanceof HibernateProxy thisProxy)
                ? thisProxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();

        if (thisEffectiveClass != oEffectiveClass) return false;
        if (!(o instanceof DigestEntry digestEntry)) return false;

        return getId() != null && Objects.equals(getId(), digestEntry.getId());
    }

    /**
     * Overrides hash code to be consistent with equals.
     * <p>
     * Uses the persistent class hash code for Hibernate proxies.
     * </p>
     *
     * @return hash code value for the entity
     */
    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy hibernateProxy) {
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        }
        return getClass().hashCode();
    }
}
