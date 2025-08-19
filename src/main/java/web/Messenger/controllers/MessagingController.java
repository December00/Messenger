package web.Messenger.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void handleMessage(Message message) {
        Chat chat = chatRepository.findById(message.getChat().getId())
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));


        message.setTimestamp(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);

        // Отправляем сообщение участникам чата
        String destination = "/topic/chat." + chat.getId();
        messagingTemplate.convertAndSend(destination, savedMessage);

        // Отправляем обновления списка всех чатов каждому подписчику
        String updateDestination1 = "/queue/chat.updates.user." + chat.getFirstId();
        String updateDestination2 = "/queue/chat.updates.user." + chat.getSecondId();

        messagingTemplate.convertAndSend(updateDestination1, savedMessage);
        messagingTemplate.convertAndSend(updateDestination2, savedMessage);
    }
}
