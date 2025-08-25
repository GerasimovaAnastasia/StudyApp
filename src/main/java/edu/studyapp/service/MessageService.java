package edu.studyapp.service;

import edu.studyapp.dto.MessageRequest;
import edu.studyapp.model.Message;
import edu.studyapp.model.MessageStatus;
import edu.studyapp.model.User;
import edu.studyapp.repository.MessageRepository;
import edu.studyapp.service.exception.ValidateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final RegisterService registerService;
    private final ValidateHandler validateHandler;
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public MessageService(MessageRepository messageRepository, RegisterService registerService, ValidateHandler validateHandler) {
        this.messageRepository = messageRepository;
        this.registerService = registerService;
        this.validateHandler = validateHandler;
    }

    public void createMessage(MessageRequest request) {
        Objects.requireNonNull(request);
        logger.info("Создание сообщения от {}, кому: {}", request.sender(), request.recipient());
        User sender = registerService.getUserByUsername(registerService.myUsername());
        User recipient = registerService.getUserByUsername(request.recipient());
        logger.info("Создание сообщения от {}, кому: {}", recipient.getUsername(), sender.getUsername());
        Message message = Message.builder()
                .status(MessageStatus.UNREAD)
                .text(request.text())
                .sender(sender)
                .recipient(recipient)
                .sentDateTime(LocalDateTime.now())
                .build();
        messageRepository.save(message);
        logger.info("Просмотр сообщений кем {}, чьих: {}", request.sender(), request.recipient());
        messageRepository.markAllAsRead(sender.getUsername(), recipient.getUsername());
    }
    public Map<String, List<Message>> chat(String recipient) {
        validateHandler.validateStringNotBlank(recipient);
        logger.info("Просмотр чата получатель {}, отправитель: {}", registerService.myUsername(), recipient);
        List<Message> allMessages = messageRepository.getMessagesByUsername(recipient, registerService.myUsername());
        List<Message> newMessages = allMessages.stream()
                .filter(m -> m.getStatus() == MessageStatus.UNREAD)
                .toList();

        Map<String, List<Message>> chatMessages = new HashMap<>();
        messageRepository.markAllAsRead(registerService.myUsername(), recipient);
        chatMessages.put("KEY_NEW_MESSAGES", newMessages);
        chatMessages.put("KEY_ALL_MESSAGES",  allMessages);
        return chatMessages;
    }
    public List<Message> messagesUnread(String recipient) {
        return messageRepository.getMessagesByStatus(recipient, registerService.myUsername(), MessageStatus.UNREAD);
    }
//    public void deleteMessage(MessageRequest request) {
//        messageRepository.delete(getMessage(request.recipient(), request.text()).getFirst());
//    }
    public List<Message> getMessage(String recipient, String text) {
        return messageRepository.getMessage(recipient, text, registerService.myUsername());
    }

//    public void updateMessage(String oldText, MessageRequest request) {
//        Message message = getMessage(request.recipient(), oldText).getFirst();
//        message.setText(request.text());
//        message.setStatus(MessageStatus.UPDATED);
//        messageRepository.save(message);
//    }

    public List<User> getRecipients(String sender) {
        return messageRepository.getMyChats(sender);

    }
    public static final String KEY_NEW_MESSAGES = "new";
    public static final String KEY_ALL_MESSAGES = "all";

}
