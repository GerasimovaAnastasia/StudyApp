package edu.studyapp.dto;

public record ApiResponse<T>(
        String message,
        T data
) {}
