package com.example.userservice.exception;

import java.util.List;

public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
    private List<String> errors;

    public ErrorResponse( int status, String message, long timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public ErrorResponse( int status, String message, long timestamp, List<String> errors) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}