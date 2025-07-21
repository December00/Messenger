package web.Messenger.controllers;

import jakarta.servlet.http.HttpSession;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ChatController {
    private Map<Long, String> getChatNames(List<Chat> chats){
        Map<Long, String> chatNames = new HashMap<>();
        for(Chat chat : chats) {
            if(!chatNames.containsKey(chat.getSecondId()))
                chatNames.put(chat.getSecondId(), userRepository.findById(chat.getSecondId()).get().getLogin());
            if(!chatNames.containsKey(chat.getFirstId()))
                chatNames.put(chat.getFirstId(), userRepository.findById(chat.getFirstId()).get().getLogin());
        }
        return chatNames;
    }
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/chat")
    public String chatPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<User> userOptional = userRepository.findById(userId);
        User user;
        List<Chat> chats = chatRepository.findAllUserChats(userId);
        if(userOptional.isPresent()) {
           user = userOptional.get();
        }
        else{
            return "redirect:/";
        }
        Map<Long, String> chatNames = getChatNames(chats);

        model.addAttribute("chats", chats);
        model.addAttribute("chatNames", chatNames);
        model.addAttribute("userId", userId);
        return "chat";
    }
    @GetMapping("/chat/{id}")
    public String currentChat(Model model){

        return "chat";
    }
    @PostMapping("/chat")
    public String addChat(@RequestParam String name, HttpSession session, Model model){
        try {
            Long userId = (Long) session.getAttribute("userId");
            Optional<User> friendOptional = userRepository.findByLogin(name);
            //Нужно будет добавить сообщение в случае отсутствия пользователя с таким именем
            if(friendOptional.isEmpty()){
                throw new Exception("Пользователь с таким ником не найден");
            }
            User friend = friendOptional.get();
            Long friendId = friend.getId();
            if(chatRepository.existsByFirstIdAndSecondId(userId, friendId) || chatRepository.existsByFirstIdAndSecondId(friendId, userId)){
                throw new Exception("Такой чат уже создан");
            }
            Chat chat = new Chat(userId, friendId);
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
