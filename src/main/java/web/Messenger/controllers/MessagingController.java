package web.Messenger.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import web.Messenger.models.Chat;
import web.Messenger.models.Message;
import web.Messenger.repo.ChatRepository;
import web.Messenger.repo.MessageRepository;

import java.time.LocalDateTime;

@Controller
public class MessagingController {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message process(Message message) {
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }
}
