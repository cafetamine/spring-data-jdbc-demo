package com.cafetamine.spring.data.jdbc.demo.core.domain.exception;


public class DataIntegrityException extends RuntimeException {

    public DataIntegrityException() {
    }

    public DataIntegrityException(final String message) {
        super(message);
    }

}
