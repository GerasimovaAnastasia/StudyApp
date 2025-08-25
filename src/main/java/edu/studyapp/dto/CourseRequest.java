package edu.studyapp.dto;

import edu.studyapp.model.Course;
import jakarta.validation.constraints.NotBlank;

public record CourseRequest(@NotBlank String title,
                            @NotBlank String description,
                            String usernameAuthor) {
    public static CourseRequest of(Course course) {
        return new CourseRequest(
                course.getTitle(),
                course.getDescription(),
                course.getAuthor().getPersonalData()
        );
    }
}
