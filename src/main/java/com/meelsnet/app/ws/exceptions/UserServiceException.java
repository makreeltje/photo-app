package com.meelsnet.app.ws.exceptions;

public class UserServiceException extends RuntimeException{

    private static final long serialVersionUID = -2000104668102247868L;

    public UserServiceException(String message) {
        super(message);
    }
}
