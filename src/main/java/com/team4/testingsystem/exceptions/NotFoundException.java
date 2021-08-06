package com.team4.testingsystem.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Not found");
    }

    public NotFoundException(String message) {
        super(message);
    }
}