package edu.studyapp;

import edu.studyapp.controller.MessageRController;
import edu.studyapp.dto.ApiResponse;
import edu.studyapp.dto.MessageRequest;
import edu.studyapp.dto.UserResponse;
import edu.studyapp.model.Message;
import edu.studyapp.model.MessageStatus;
import edu.studyapp.model.User;
import edu.studyapp.repository.MessageRepository;
import edu.studyapp.service.MessageService;
import edu.studyapp.service.RegisterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageRControllerTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private RegisterService registerService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageRController messageRController;

    @Test
    void myChats_Success() {

        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        when(registerService.myUsername()).thenReturn("currentUser");
        when(messageRepository.getMyChats("currentUser")).thenReturn(List.of(user1, user2));

        ResponseEntity<?> response = messageRController.myChats();

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());

        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();
        assertEquals("Список Ваших чатов:", apiResponse.message());
    }

    @Test
    void chatByRecipient_Success() {
        User sender = new User();
        sender.setUsername("senderUser");
        User recipient = new User();
        recipient.setUsername("recipientUser");

        Message message1 = new Message();
        message1.setSender(sender);
        message1.setRecipient(recipient);
        message1.setText("Hello!");

        Message message2 = new Message();
        message2.setSender(recipient);
        message2.setRecipient(sender);
        message2.setText("Hi!");

        when(messageService.chat("recipientUser"))
                .thenReturn(Map.of("KEY_ALL_MESSAGES", List.of(message1, message2)));

        ResponseEntity<?> response = messageRController.chatByRecipient("recipientUser");

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
    }

    @Test
    void createMessage_Success() {
        MessageRequest request = new MessageRequest("Hello",
                "recipientUser", "senderUser",
                MessageStatus.UNREAD,
                LocalDateTime.now());
        User sender = new User();
        sender.setUsername("senderUser");
        User recipient = new User();
        recipient.setUsername("recipientUser");

        Message message1 = new Message();
        message1.setSender(sender);
        message1.setRecipient(recipient);
        message1.setText("Hello");

        Message message2 = new Message();
        message2.setSender(recipient);
        message2.setRecipient(sender);
        message2.setText("Hi back!");

        doNothing().when(messageService).createMessage(request);
        when(messageService.chat("recipientUser"))
                .thenReturn(Map.of("KEY_ALL_MESSAGES", List.of(message1, message2)));

        ResponseEntity<?> response = messageRController.createMessage(request, "recipientUser");

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(ApiResponse.class, response.getBody());
        verify(messageService, times(1)).createMessage(request);
    }
}
