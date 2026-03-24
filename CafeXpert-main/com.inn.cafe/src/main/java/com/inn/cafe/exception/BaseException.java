package com.inn.cafe.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends Exception {
    
    private int statusCode;
    
    public BaseException(String message, String statusCodeStr) {
        super(message);
        try {
            this.statusCode = Integer.parseInt(statusCodeStr);
        } catch (NumberFormatException e) {
            // Default to 500 if string is not a valid int
            this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
    }
    
    public BaseException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(statusCode);
    }
}
