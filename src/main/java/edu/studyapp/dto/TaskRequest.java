package edu.studyapp.dto;

import edu.studyapp.model.Task;
import jakarta.validation.constraints.NotBlank;

public record TaskRequest(@NotBlank String title,
                          @NotBlank String taskContent,
                          String titleCourse) {
    public static TaskRequest of(Task task) {
        return new TaskRequest(
                task.getTitle(),
                task.getTaskContent(),
                task.getCourse().getTitle()
        );
    }
}
