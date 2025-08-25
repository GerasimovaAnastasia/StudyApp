package edu.studyapp.dto;

import edu.studyapp.model.Task;
import edu.studyapp.model.User;
import jakarta.validation.constraints.NotBlank;

public record UserResponse(@NotBlank String firstName,
                           @NotBlank String lastName,
                           @NotBlank String role) {
    public static UserResponse of(User user) {
        return new UserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }
}
