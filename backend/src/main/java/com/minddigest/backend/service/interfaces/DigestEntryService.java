package com.minddigest.backend.service.interfaces;

import com.minddigest.backend.dto.DigestEntryDto;

/**
 * Service interface for managing {@link DigestEntryDto} entities.
 * <p>
 * This interface extends the generic {@link CRUDable} interface, inheriting basic CRUD (Create, Read, Update, Delete)
 * operations for {@link DigestEntryDto} objects. It provides service-level methods for manipulating
 * digest entry data in the application.
 * </p>
 *
 * <p>The service implementation should provide business logic on top of the basic CRUD operations,
 * such as validation, logging, and additional processing that is specific to {@link DigestEntryDto} objects.</p>
 *
 * @see CRUDable
 * @see DigestEntryDto
 */
public interface DigestEntryService extends CRUDable<DigestEntryDto> {
}
