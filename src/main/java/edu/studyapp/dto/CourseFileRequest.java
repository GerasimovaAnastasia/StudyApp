package edu.studyapp.dto;

import edu.studyapp.model.CourseFile;
import jakarta.validation.constraints.NotBlank;

public record CourseFileRequest(@NotBlank String fileName,
                                @NotBlank String fileType,
                                @NotBlank String filePath,
                                String titleCourse) {
    public static CourseFileRequest of(CourseFile courseFile) {
        return new CourseFileRequest(
                courseFile.getFileName(),
                courseFile.getFileType(),
                courseFile.getFilePath(),
                courseFile.getCourse().getTitle()
        );
    }
}
