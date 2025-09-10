package com.minddigest.backend.exception;

import java.io.Serial;
import java.io.Serializable;


/**
 * Abstract base class for custom exceptions used in the backend.
 * <p>
 * This class serves as the base for exceptions related to resource validation and error handling in the backend.
 * It holds information about the resource, field, and field value that caused the exception to be thrown.
 * Subclasses of this class should provide specific exception messages and behaviors.
 * </p>
 *
 * <p>Example:</p>
 * <pre>
 * throw new ResourceNotFoundException("not found", "User", "id", userId);
 * </pre>
 *
 * @see ResourceNotFoundException
 * @see ResourceAlreadyExistsException
 * @see BadRequestException
 */
public abstract class BackendWebException extends Exception {

    @Serial
    private static final long serialVersionUID = -2713300573235999908L;

    /**
     * The name of the resource that the exception relates to.
     */
    private final String resourceName;

    /**
     * The name of the field that caused the exception to be thrown.
     */
    private final String fieldName;

    /**
     * The value of the field that caused the exception to be thrown.
     */
    private final Serializable fieldValue;

    /**
     * Constructs a new BackendWebException with the specified message and details.
     * <p>
     * The message is formatted to include the resource name, message, field name, and field value.
     * </p>
     *
     * @param msg          the message to be included in the exception
     * @param resourceName the name of the resource
     * @param fieldName    the name of the field
     * @param fieldValue   the value of the field
     */
    protected BackendWebException(String msg, String resourceName, String fieldName, Serializable fieldValue) {
        super(String.format("%s %s %s : '%s'", resourceName, msg, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Returns the name of the resource related to this exception.
     *
     * @return the resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Returns the name of the field related to this exception.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the value of the field related to this exception.
     *
     * @return the field value
     */
    public Serializable getFieldValue() {
        return fieldValue;
    }

}
