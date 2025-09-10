package com.minddigest.backend.service.interfaces;

import com.minddigest.backend.exception.ResourceAlreadyExistsException;
import com.minddigest.backend.exception.ResourceNotFoundException;

import java.util.List;


/**
 * Generic interface that defines the basic CRUD (Create, Read, Update, Delete) operations
 * for managing entities of type {@code T}.
 * <p>
 * This interface provides method signatures for standard CRUD operations that can be implemented
 * by any service class responsible for handling entities or domain objects of type {@code T}.
 * </p>
 *
 * <p>Implementing classes should provide the logic for interacting with the data store (e.g., database).</p>
 *
 * @param <T> the type of the domain object this interface will handle (e.g., User, Product)
 */
public interface CRUDable<T> {

    /**
     * Retrieves all entities of type {@code T}.
     * <p>
     * This method is expected to return a list of all records of the specified type from the data store.
     * </p>
     *
     * @return a list of all entities of type {@code T}
     */
    List<T> listAll();

    /**
     * Retrieves an entity of type {@code T} by its unique identifier.
     * <p>
     * If the entity is not found, a {@link ResourceNotFoundException} is thrown.
     * </p>
     *
     * @param id the unique identifier of the entity to retrieve
     * @return the entity of type {@code T} with the specified ID
     * @throws ResourceNotFoundException if no entity with the specified ID is found
     */
    T getById(Long id) throws ResourceNotFoundException;

    /**
     * Saves a new entity of type {@code T} to the data store.
     * <p>
     * If the entity already exists (determined by the presence of its ID), a {@link ResourceAlreadyExistsException}
     * will be thrown.
     * </p>
     *
     * @param domainObject the entity to save
     * @return the saved entity
     * @throws ResourceAlreadyExistsException if an entity with the same ID already exists
     */
    T save(T domainObject) throws ResourceAlreadyExistsException;

    /**
     * Updates an existing entity of type {@code T}.
     * <p>
     * If the entity with the specified ID does not exist, a {@link ResourceNotFoundException} will be thrown.
     * </p>
     *
     * @param id           the identifier of the entity to update
     * @param domainObject the entity to update
     * @return the updated entity
     * @throws ResourceNotFoundException if the entity to update does not exist
     */
    T update(Long id, T domainObject) throws ResourceNotFoundException;

    /**
     * Deletes an entity of type {@code T} by its unique identifier.
     * <p>
     * If no entity with the specified ID is found, a {@link ResourceNotFoundException} will be thrown.
     * </p>
     *
     * @param id the unique identifier of the entity to delete
     * @throws ResourceNotFoundException if no entity with the specified ID exists
     */
    void delete(Long id) throws ResourceNotFoundException;
}
