package edu.studyapp.dto;

import edu.studyapp.model.StudentAnswer;
import edu.studyapp.model.Task;
import jakarta.validation.constraints.NotBlank;

public record StudentAnswerRequest(String username,
                                   String titleTask,
                                   @NotBlank String text,
                                   Integer grade) {

    public static StudentAnswerRequest of(StudentAnswer studentAnswer) {
        return new StudentAnswerRequest(
                studentAnswer.getStudent().getUsername(),
                studentAnswer.getTask().getTitle(),
                studentAnswer.getAnswerText(),
                studentAnswer.getGrade()
        );
    }
}
