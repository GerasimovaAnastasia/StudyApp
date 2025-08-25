package edu.studyapp.dto;

import edu.studyapp.model.Literature;
import jakarta.validation.constraints.NotBlank;

public record LiteratureRequest(@NotBlank String title,
                                @NotBlank String authorBook,
                                @NotBlank String link,
                                String titleCourse) {
    public static LiteratureRequest of(Literature literature) {
        return new LiteratureRequest(
                literature.getTitle(),
                literature.getAuthor(),
                literature.getLink(),
                literature.getCourse().getTitle()
        );
    }
}
