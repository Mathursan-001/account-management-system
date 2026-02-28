package com.rhb.ams.exception;

import lombok.Getter;

/**
 * Exception thrown when an external service call fails
 */
@Getter
public class ExternalServiceException extends RuntimeException {

    private String serviceName;
    private int statusCode;

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String serviceName, String message, int statusCode) {
        super(String.format("External service '%s' error: %s (Status: %d)", serviceName, message, statusCode));
        this.serviceName = serviceName;
        this.statusCode = statusCode;
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
