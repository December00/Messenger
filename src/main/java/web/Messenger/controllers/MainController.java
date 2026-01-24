package web.Messenger.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.Messenger.models.Session;
import web.Messenger.models.User;
import web.Messenger.repo.SessionRepository;
import web.Messenger.repo.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
    private SessionRepository sessionRepository;
    @Autowired
    private UserRepository userRepository;
    private String checkSessionAndRedirect(HttpServletRequest request, Model model) {
        // Проверяем куки на наличие токена сессии
        Cookie[] cookies = request.getCookies();
        String sessionToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionToken".equals(cookie.getName())) {
                    sessionToken = cookie.getValue();
                    break;
                }
            }
        }

        if (sessionToken != null) {
            Optional<Session> sessionOpt = sessionRepository.findValidSession(
                    sessionToken, LocalDateTime.now()
            );

            if (sessionOpt.isPresent()) {
                Session session = sessionOpt.get();
                // Автоматически авторизуем пользователя
                HttpSession httpSession = request.getSession();
                httpSession.setAttribute("userId", session.getUserId());
                return "redirect:/chat";
            } else {
                Cookie cookie = new Cookie("sessionToken", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
            }
        }

        // Если сессия не найдена или просрочена, показываем страницу авторизации
        return "registration";
    }
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        return checkSessionAndRedirect(request, model);
    }

    @GetMapping("/register")
    public String reg(HttpServletRequest request, Model model) {
        return checkSessionAndRedirect(request, model);
    }

    @GetMapping("/login")
    public String log(HttpServletRequest request, Model model) {
        return checkSessionAndRedirect(request, model);
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
    public String userLogin(@RequestParam String login, @RequestParam String password, Model model, HttpSession session,
                            HttpServletResponse response) throws NoSuchAlgorithmException {
        Optional<User> userOptional = userRepository.findByLogin(login);


        if (userOptional.isEmpty() || !userOptional.get().getPassword().equals(bytesToHex(encodePassword(password)))) {
            model.addAttribute("error", "Неверный логин или пароль");
            return "registration";
        }
        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        Session newSession = new Session(user.getId(), token);
        sessionRepository.save(newSession);
        session.setAttribute("userId", user.getId());

        Cookie sessionCookie = new Cookie("sessionToken", token);
        sessionCookie.setMaxAge(14 * 24 * 60 * 60); // == 14 дней
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        response.addCookie(sessionCookie);
        return "redirect:/chat";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Cookie[] cookies = request.getCookies();
        String sessionToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionToken".equals(cookie.getName())) {
                    sessionToken = cookie.getValue();
                    break;
                }
            }
        }
        // Если токен есть удаляю сессию из БД
        if (sessionToken != null) {
            Optional<Session> sessionOpt = sessionRepository.findByToken(sessionToken);
            sessionOpt.ifPresent(sessionRepository::delete);
        }
        // Удаляю куку из браузера
        Cookie cookie = new Cookie("sessionToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        if (request.isSecure()) {
            cookie.setSecure(true);
        }
        response.addCookie(cookie);
        // Завершаю сессию
        session.invalidate();

        return "redirect:/login";
    }
}