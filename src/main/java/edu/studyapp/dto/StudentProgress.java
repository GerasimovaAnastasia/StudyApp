package edu.studyapp.dto;

import edu.studyapp.model.Student;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public record StudentProgress(Student student,
                              int completedTasks,
                              double progressPercent) {
}
