package edu.studyapp.dto;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public record ProfileUpdateRequest(
        String firstName,
        String lastName,
        String groupName,
        String position
) {

}