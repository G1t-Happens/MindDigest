package com.minddigest.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.io.Serializable;


/**
 * Exception thrown when a bad request is made, typically for invalid or disallowed values.
 * <p>
 * This exception is typically used in cases where the request is malformed or the data submitted is invalid,
 * such as when a resource is not in the expected format or violates business rules.
 * It returns a 400 Bad Request HTTP status to indicate the issue with the client request.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * throw new BadRequestException("User", "email", "invalid-email");
 * </pre>
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends BackendWebException {

    @Serial
    private static final long serialVersionUID = -7402329910102653858L;

    /**
     * Constructs a new BadRequestException with a specified resource name, field name, and field value.
     * <p>
     * The exception message is formatted as "invalid or not allowed value" for the given resource and field.
     * </p>
     *
     * @param resourceName the name of the resource that the exception relates to (e.g., "User", "Product")
     * @param fieldName    the name of the field that caused the exception (e.g., "email", "price")
     * @param fieldValue   the value of the field that caused the exception (e.g., "invalid-email", 123)
     */
    public BadRequestException(String resourceName, String fieldName, Serializable fieldValue) {
        super("invalid or not allowed value", resourceName, fieldName, fieldValue);
    }
}
