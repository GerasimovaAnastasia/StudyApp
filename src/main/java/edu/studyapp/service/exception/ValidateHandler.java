package edu.studyapp.service.exception;

import edu.studyapp.service.CourseFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ValidateHandler {
    private static final Logger logger = LoggerFactory.getLogger(ValidateHandler.class);

    public void validateStringNotBlank(String value) {
        if (value == null || value.isBlank()) {
            String errorMessage = "Параметр не может быть пустым";
            logger.warn("{}: {}", "validateStringNotBlank", errorMessage);
            throw new IllegalArgumentException("errorMessage");
        }
    }
}
