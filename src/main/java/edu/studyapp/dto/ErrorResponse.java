package edu.studyapp.dto;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String message,
        String timestamp
) {
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(
                status,
                message,
                Instant.now().toString()
        );
    }
}
