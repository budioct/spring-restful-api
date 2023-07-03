package com.tutorial.restful.api.Exception;

public class ApiException extends RuntimeException{

    // class ini yang akan handle Exception.. secara manual

    public ApiException(String message) {
        super(message);
    }
}
