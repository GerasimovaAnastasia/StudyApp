package edu.studyapp.dto;

import edu.studyapp.model.Student;
import edu.studyapp.model.Teacher;
import jakarta.validation.constraints.NotBlank;

public record RegisterTeacherRequest(@NotBlank String username,
                                     @NotBlank String password,
                                     @NotBlank String firstName,
                                     @NotBlank String lastName,
                                     @NotBlank String position) {
    public static RegisterTeacherRequest of(Teacher teacher) {
        return new RegisterTeacherRequest(
                teacher.getUsername(),
                teacher.getPassword(),
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getPosition()
        );
    }
}
