package edu.studyapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.studyapp.model.StudentCourse;
import edu.studyapp.model.Task;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record StudentCourseRequest(@NotBlank String username,
                                   @NotBlank String titleCourse,
                                   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                   LocalDateTime data,
                                   @NotBlank String status) {
    public static StudentCourseRequest of(StudentCourse studentCourse) {
        return new StudentCourseRequest(
                studentCourse.getStudent().getUsername(),
                studentCourse.getCourse().getTitle(),
                studentCourse.getStartDate(),
                studentCourse.getStatus().name()
        );
    }
}
