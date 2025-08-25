package edu.studyapp;

import edu.studyapp.dto.MessageRequest;
import edu.studyapp.model.Message;
import edu.studyapp.model.MessageStatus;
import edu.studyapp.model.StudentCourse;
import edu.studyapp.model.User;
import edu.studyapp.repository.MessageRepository;
import edu.studyapp.service.MessageService;
import edu.studyapp.service.RegisterService;
import edu.studyapp.service.exception.ValidateHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private RegisterService registerService;

    @Mock
    private ValidateHandler validateHandler;

    @InjectMocks
    private MessageService messageService;

    @Test
    void createMessage_Success() {

        MessageRequest request = new MessageRequest("Hello!",
                "recipientUser",
                "senderUser",
                MessageStatus.UNREAD,
                LocalDateTime.now());
        User sender = new User();
        sender.setUsername("currentUser");
        User recipient = new User();
        recipient.setUsername("recipientUser");

        when(registerService.myUsername()).thenReturn("currentUser");
        when(registerService.getUserByUsername("currentUser")).thenReturn(sender);
        when(registerService.getUserByUsername("recipientUser")).thenReturn(recipient);


        messageService.createMessage(request);

        verify(messageRepository, times(1)).save(any(Message.class));
        verify(messageRepository, times(1)).markAllAsRead("currentUser", "recipientUser");
    }

    @Test
    void chat_Success() {

        Message message1 = new Message();
        Message message2 = new Message();
        message2.setStatus(MessageStatus.UNREAD);

        when(registerService.myUsername()).thenReturn("user1");
        when(messageRepository.getMessagesByUsername("user2", "user1"))
                .thenReturn(List.of(message1, message2));

        Map<String, List<Message>> result = messageService.chat("user2");

        assertNotNull(result);
        assertEquals(2, result.get("KEY_ALL_MESSAGES").size());
        assertEquals(1, result.get("KEY_NEW_MESSAGES").size());
        verify(messageRepository, times(1)).markAllAsRead("user1", "user2");
    }

    @Test
    void messagesUnread_Success() {

        Message message = new Message();
        when(registerService.myUsername()).thenReturn("user1");
        when(messageRepository.getMessagesByStatus("user2", "user1", MessageStatus.UNREAD))
                .thenReturn(List.of(message));

        List<Message> result = messageService.messagesUnread("user2");

        assertEquals(1, result.size());
    }

    @Test
    void getMessage_Success() {

        Message message = new Message();
        when(registerService.myUsername()).thenReturn("user1");
        when(messageRepository.getMessage("user2", "Hello", "user1"))
                .thenReturn(List.of(message));

        List<Message> result = messageService.getMessage("user2", "Hello");

        assertEquals(1, result.size());
    }

    @Test
    void getRecipients_Success() {
        User user = new User();
        when(messageRepository.getMyChats("user1")).thenReturn(List.of(user));

        List<User> result = messageService.getRecipients("user1");

        assertEquals(1, result.size());
    }

}

