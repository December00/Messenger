package web.Messenger.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.Messenger.models.User;
import web.Messenger.repo.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
//Регистрация и аутентификация
@Controller
public class MainController {
    private byte[] encodePassword(String password) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    }
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
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
    public String userRegister(@RequestParam String login, @RequestParam String password, Model model) throws NoSuchAlgorithmException {
        if (userRepository.findByLogin(login).isPresent()) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "registration";
        }

        User newUser = new User(login, bytesToHex(encodePassword(password)));
        userRepository.save(newUser);

        model.addAttribute("success", "Регистрация прошла успешно. Теперь вы можете войти.");
        return "registration";
    }
    @PostMapping("/login")
    public String userLogin(@RequestParam String login, @RequestParam String password, Model model, HttpSession session) throws NoSuchAlgorithmException {
        Optional<User> userOptional = userRepository.findByLogin(login);


        if (userOptional.isEmpty() || !userOptional.get().getPassword().equals(bytesToHex(encodePassword(password)))) {
            model.addAttribute("error", "Неверный логин или пароль");
            return "registration";
        }
        User user = userOptional.get();
        model.addAttribute("success", "Вход выполнен успешно");
        session.setAttribute("userId", user.getId());
        
        return "redirect:/chat";
    }
}