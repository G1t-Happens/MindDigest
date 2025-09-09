package com.minddigest.backend.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Base abstract entity class providing common auditing fields for all entities.
 * <p>
 * Includes creation and last modification timestamps, which are automatically
 * managed by Spring Data JPA auditing.
 * </p>
 *
 * <p>Entities extending this class inherit the {@code created} and {@code updated} timestamps.</p>
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4321220625746217297L;

    /**
     * Timestamp indicating when the entity was created.
     * Automatically set on persist, never updated afterwards.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant created;

    /**
     * Timestamp indicating the last time the entity was updated.
     * Automatically updated on every entity update.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private Instant updated;

    /**
     * Returns the creation timestamp of the entity.
     *
     * @return the creation time as an {@link Instant}
     */
    public Instant getCreated() {
        return created;
    }

    /**
     * Returns the last modification timestamp of the entity.
     *
     * @return the last update time as an {@link Instant}
     */
    public Instant getUpdated() {
        return updated;
    }
}
