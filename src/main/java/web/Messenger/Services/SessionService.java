package web.Messenger.Services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.Messenger.models.Session;
import web.Messenger.repo.SessionRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public Long getUserIdFromSessionOrCookie(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        // Сначала проверяем существующую HTTP сессию
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            return userId;
        }

        // Если в HTTP сессии нет, проверяем куки
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
            try {
                Optional<Session> sessionOpt = sessionRepository.findValidSession(sessionToken, LocalDateTime.now());

                if (sessionOpt.isPresent()) {
                    userId = sessionOpt.get().getUserId();
                    // Сохраняем в HTTP сессию, чтобы не проверять куки каждый раз
                    session.setAttribute("userId", userId);
                    return userId;
                } else {
                    // Удаляем просроченную куку
                    Cookie cookie = new Cookie("sessionToken", null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            } catch (Exception e) {
                System.err.println("Ошибка при проверке сессии: " + e.getMessage());
            }
        }

        return null;
    }
    public void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        // Удаляем из HTTP сессии
        session.removeAttribute("userId");
        session.invalidate();
        // Удаляем куку
        Cookie cookie = new Cookie("sessionToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}