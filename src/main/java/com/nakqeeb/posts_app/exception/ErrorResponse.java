package com.nakqeeb.posts_app.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private int status;
    private String description;

    public ErrorResponse(String message, int status, String description) {
        this.message = message;
        this.status = status;
        this.description = description;
    }
}