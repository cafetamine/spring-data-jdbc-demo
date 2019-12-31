package com.cafetamine.spring.data.jdbc.demo.core.domain.exception;


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(final String message) {
        super(message);
    }

}
