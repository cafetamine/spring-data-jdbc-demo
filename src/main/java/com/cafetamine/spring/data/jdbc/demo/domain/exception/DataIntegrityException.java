package com.cafetamine.spring.data.jdbc.demo.domain.exception;


public class DataIntegrityException extends RuntimeException {

    public DataIntegrityException() {
    }

    public DataIntegrityException(final String message) {
        super(message);
    }

}
