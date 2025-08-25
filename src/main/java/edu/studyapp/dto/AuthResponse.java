package edu.studyapp.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn
) {

    public static AuthResponse of(String token, long expiresInMillis) {
        return new AuthResponse(
                token,
                "Bearer",
                expiresInMillis / 1000
        );
    }
}
