package web.Messenger.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import web.Messenger.models.User;
import web.Messenger.repo.UserRepository;

import java.util.Optional;

@Controller
public class ProfileController {
    @Autowired
    private web.Messenger.Services.SessionService sessionService;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/profile/{id}")
    private String profilePage(@PathVariable(value = "id") Long uid, Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        Long userId = sessionService.getUserIdFromSessionOrCookie(request, response, session);
        if (userId == null) {
            return "redirect:/login";
        }
        if (userId.equals(uid)){
            return "redirect:/settings/" + uid;
        }
        Optional<User> user = userRepository.findById(uid);
        model.addAttribute("user", user.get());

        return "profile";
    }
}
