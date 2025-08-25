package edu.studyapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.studyapp.model.Message;
import edu.studyapp.model.MessageStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record MessageRequest(@NotBlank String text,
                             String recipient,
                             String sender,
                             MessageStatus status,
                             @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                             LocalDateTime sentDateTime) {
    public static MessageRequest of(Message message) {
        return new MessageRequest(
                message.getText(),
                message.getRecipient().getUsername(),
                message.getSender().getUsername(),
                message.getStatus(),
                message.getSentDateTime()
        );
    }
}
