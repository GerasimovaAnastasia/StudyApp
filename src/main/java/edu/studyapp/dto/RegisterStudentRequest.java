package edu.studyapp.dto;

import edu.studyapp.model.Student;
import jakarta.validation.constraints.NotBlank;

public record RegisterStudentRequest(@NotBlank String username,
                                     @NotBlank String password,
                                     @NotBlank String firstName,
                                     @NotBlank String lastName,
                                     @NotBlank String groupName) {
    public static RegisterStudentRequest of(Student student) {
        return new RegisterStudentRequest(
                student.getUsername(),
                student.getPassword(),
                student.getFirstName(),
                student.getLastName(),
                student.getGroupName()
        );
    }
}
