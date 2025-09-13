package com.legalfirm.automation.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;

/**
 * Exception thrown when a client request is malformed or contains invalid data.
 * This exception results in an HTTP 400 Bad Request response.
 */
@Setter
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * -- GETTER --
     *  Gets the error code associated with this exception.
     *
     *
     * -- SETTER --
     *  Sets the error code for this exception.
     *
     @return the error code, or null if not set
      * @param errorCode the error code to set
     */
    private String errorCode;
    /**
     * -- GETTER --
     *  Gets the additional data associated with this exception.
     *
     *
     * -- SETTER --
     *  Sets the additional data for this exception.
     *
     @return the additional data map, or null if not set
      * @param additionalData the additional data to set
     */
    private Map<String, Object> additionalData;

    /**
     * Constructs a new BadRequestException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new BadRequestException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause   the cause of this exception
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new BadRequestException with the specified detail message and error code.
     *
     * @param message   the detail message explaining the reason for the exception
     * @param errorCode a specific error code for this exception
     */
    public BadRequestException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new BadRequestException with the specified detail message, error code, and cause.
     *
     * @param message   the detail message explaining the reason for the exception
     * @param errorCode a specific error code for this exception
     * @param cause     the cause of this exception
     */
    public BadRequestException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new BadRequestException with the specified detail message and additional data.
     *
     * @param message        the detail message explaining the reason for the exception
     * @param additionalData additional data related to the exception
     */
    public BadRequestException(String message, Map<String, Object> additionalData) {
        super(message);
        this.additionalData = additionalData;
    }

    /**
     * Constructs a new BadRequestException with all parameters.
     *
     * @param message        the detail message explaining the reason for the exception
     * @param errorCode      a specific error code for this exception
     * @param additionalData additional data related to the exception
     * @param cause          the cause of this exception
     */
    public BadRequestException(String message, String errorCode, Map<String, Object> additionalData, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.additionalData = additionalData;
    }

    /**
     * Static factory method for creating BadRequestException with common validation messages.
     */
    public static class Builder {

        /**
         * Creates a BadRequestException for invalid input parameters.
         *
         * @param fieldName the name of the invalid field
         * @param value     the invalid value
         * @return a new BadRequestException
         */
        public static BadRequestException invalidField(String fieldName, Object value) {
            return new BadRequestException(
                    String.format("Invalid value '%s' for field '%s'", value, fieldName),
                    "INVALID_FIELD_VALUE"
            );
        }

        /**
         * Creates a BadRequestException for missing required parameters.
         *
         * @param parameterName the name of the missing parameter
         * @return a new BadRequestException
         */
        public static BadRequestException missingParameter(String parameterName) {
            return new BadRequestException(
                    String.format("Required parameter '%s' is missing", parameterName),
                    "MISSING_PARAMETER"
            );
        }

        /**
         * Creates a BadRequestException for invalid file uploads.
         *
         * @param reason the specific reason why the file is invalid
         * @return a new BadRequestException
         */
        public static BadRequestException invalidFile(String reason) {
            return new BadRequestException(
                    String.format("Invalid file: %s", reason),
                    "INVALID_FILE"
            );
        }

        /**
         * Creates a BadRequestException for invalid date ranges.
         *
         * @param startDate the start date
         * @param endDate   the end date
         * @return a new BadRequestException
         */
        public static BadRequestException invalidDateRange(String startDate, String endDate) {
            return new BadRequestException(
                    String.format("Invalid date range: start date '%s' must be before end date '%s'", startDate, endDate),
                    "INVALID_DATE_RANGE"
            );
        }

        /**
         * Creates a BadRequestException for duplicate entries.
         *
         * @param entityType the type of entity that's duplicated
         * @param identifier the identifier of the duplicate entity
         * @return a new BadRequestException
         */
        public static BadRequestException duplicateEntry(String entityType, String identifier) {
            return new BadRequestException(
                    String.format("%s with identifier '%s' already exists", entityType, identifier),
                    "DUPLICATE_ENTRY"
            );
        }

        /**
         * Creates a BadRequestException for business rule violations.
         *
         * @param ruleName    the name of the violated rule
         * @param description a description of the violation
         * @return a new BadRequestException
         */
        public static BadRequestException businessRuleViolation(String ruleName, String description) {
            return new BadRequestException(
                    String.format("Business rule violation - %s: %s", ruleName, description),
                    "BUSINESS_RULE_VIOLATION"
            );
        }

        /**
         * Creates a BadRequestException for invalid state transitions.
         *
         * @param currentState the current state
         * @param targetState  the target state
         * @param entityType   the type of entity
         * @return a new BadRequestException
         */
        public static BadRequestException invalidStateTransition(String currentState, String targetState, String entityType) {
            return new BadRequestException(
                    String.format("Cannot transition %s from state '%s' to '%s'", entityType, currentState, targetState),
                    "INVALID_STATE_TRANSITION"
            );
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(": ");

        if (errorCode != null) {
            sb.append("[").append(errorCode).append("] ");
        }

        sb.append(getMessage());

        if (additionalData != null && !additionalData.isEmpty()) {
            sb.append(" (Additional data: ").append(additionalData).append(")");
        }

        return sb.toString();
    }
}