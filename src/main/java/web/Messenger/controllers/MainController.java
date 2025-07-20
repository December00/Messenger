package web.Messenger.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.Messenger.models.User;
import web.Messenger.repo.UserRepository;

import java.util.Optional;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/")
    public String index(Model model) {
        //model.addAttribute();
        return "registration";
    }
    @GetMapping("/register")
    public String reg(Model model) {
        //model.addAttribute();
        return "registration";
    }
    @GetMapping("/login")
    public String log(Model model) {
        //model.addAttribute();
        return "registration";
    }
    @PostMapping("/register")
    public String userRegister(@RequestParam String login, @RequestParam String password, Model model) {
        if (userRepository.findByLogin(login).isPresent()) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "registration";
        }

        User newUser = new User(login, password);
        userRepository.save(newUser);

        model.addAttribute("success", "Регистрация прошла успешно. Теперь вы можете войти.");
        return "registration";
    }
    @PostMapping("/login")
    public String userLogin(@RequestParam String login, @RequestParam String password, Model model) {
        Optional<User> userOptional = userRepository.findByLogin(login);


        if (userOptional.isEmpty() || !userOptional.get().getPassword().equals(password)) {
            model.addAttribute("error", "Неверный логин или пароль");
            return "registration";
        }
        User user = userOptional.get();
        model.addAttribute("success", "Вход выполнен успешно");
        //model.addAttribute("userId", user.getId());
        return "redirect:/chat";
    }
}