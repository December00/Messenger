package web.Messenger.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.Messenger.models.Chat;
import web.Messenger.models.User;
import web.Messenger.repo.MessageRepository;
import web.Messenger.repo.ChatRepository;
import web.Messenger.repo.UserRepository;

import java.util.Optional;

@Controller
public class ChatController {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/chat")
    public String chatPage(Model model) {
        model.addAttribute("chats", chatRepository.findAllUserChats(5L));
        return "chat";
    }
    @GetMapping("/chat/{id}")
    public String currentChat(Model model){

        return "chat";
    }
    @PostMapping("/chat")
    public String addChat(@RequestParam String name, Model model){
        try {
            Optional<User> friendOptional = userRepository.findByLogin(name);
            //Нужно будет добавить сообщение в случае отсутствия пользователя с таким именем
            User friend = friendOptional.get();
            Long friendId = friend.getId();
            if(chatRepository.existsByFirstIdAndSecondId(5L, friendId) || chatRepository.existsByFirstIdAndSecondId(friendId, 5L)){
                throw new Exception("Такой чат уже создан");
            }
            Chat chat = new Chat(5L, friendId);
            chatRepository.save(chat);

        }
        catch(Exception e) {
            model.addAttribute("error", e);
            return "registration";
        }

        return "redirect:/chat";
    }
    @PostMapping("/chat/{id}")
    public String printMessage(Model model){
        return "chat";
    }
}
