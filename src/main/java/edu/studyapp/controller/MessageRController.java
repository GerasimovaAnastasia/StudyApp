package edu.studyapp.controller;

import edu.studyapp.dto.ApiResponse;
import edu.studyapp.dto.MessageRequest;
import edu.studyapp.dto.UserResponse;
import edu.studyapp.model.Message;
import edu.studyapp.model.User;
import edu.studyapp.repository.MessageRepository;
import edu.studyapp.service.MessageService;
import edu.studyapp.service.RegisterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('STUDENT') || hasRole('TEACHER')")
public class MessageRController {
    private final MessageRepository messageRepository;
    private final RegisterService registerService;
    private final MessageService messageService;

    public MessageRController(MessageRepository messageRepository, RegisterService registerService, MessageService messageService) {
        this.messageRepository = messageRepository;
        this.registerService = registerService;
        this.messageService = messageService;
    }

    @GetMapping("/myChats")
    public ResponseEntity<?> myChats() {
        List<User> userList = messageRepository.getMyChats(registerService.myUsername());
        return ResponseEntity.ok(new ApiResponse<>("Список Ваших чатов:",
                userList.stream()
                        .map(UserResponse::of)
                        .toList())
        );
    }
    @GetMapping("/myChats/{recipient}")
    public ResponseEntity<?> chatByRecipient(@PathVariable String recipient) {
        Map<String, List<Message>> massageList = messageService.chat(recipient);
        return ResponseEntity.ok(new ApiResponse<>("Чат с " + recipient + ": ",
                massageList.get("KEY_ALL_MESSAGES").stream()
                        .map(MessageRequest::of)
                        .toList())
        );
    }

    @PostMapping("/myChats/{recipient}/create")
    public ResponseEntity<?> createMessage(@RequestBody MessageRequest request,
            @PathVariable String recipient) {
        messageService.createMessage(request);
        Map<String, List<Message>> massageList = messageService.chat(recipient);
        return ResponseEntity.ok(new ApiResponse<>("Чат с " + recipient + ": ",
                massageList.get("KEY_ALL_MESSAGES").stream()
                        .map(MessageRequest::of)
                        .toList())
        );
    }

}
