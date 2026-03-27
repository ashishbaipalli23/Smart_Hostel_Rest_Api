package com.hostel.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String msg) {
        super(msg);
    }
}
