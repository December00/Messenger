package web.Messenger.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String statusText = "Ошибка сервера";

        if (status != null) {
            try {
                int statusCode = Integer.parseInt(status.toString());
                httpStatus = HttpStatus.valueOf(statusCode);
                statusText = getStatusText(httpStatus);

                model.addAttribute("statusCode", statusCode);
            } catch (NumberFormatException e) {
                model.addAttribute("statusCode", 500);
            }
        } else {
            model.addAttribute("statusCode", 500);
        }

        model.addAttribute("statusText", statusText);

        // Если есть сообщение об ошибке, используем его, иначе стандартное
        if (message != null && !message.toString().isEmpty()) {
            model.addAttribute("message", message);
        } else {
            model.addAttribute("message", httpStatus.getReasonPhrase());
        }

        // Если есть исключение, добавляем его краткую информацию
        if (exception != null) {
            Exception ex = (Exception) exception;
            model.addAttribute("exception", ex.getClass().getSimpleName());
        }

        model.addAttribute("path", requestUri);
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error.html";
    }

    private String getStatusText(HttpStatus status) {
        return switch (status.value()) {
            case 400 -> "Неверный запрос";
            case 401 -> "Не авторизован";
            case 403 -> "Доступ запрещен";
            case 404 -> "Страница не найдена";
            case 405 -> "Метод не разрешен";
            case 408 -> "Время ожидания истекло";
            case 500 -> "Внутренняя ошибка сервера";
            case 502 -> "Плохой шлюз";
            case 503 -> "Сервис недоступен";
            case 504 -> "Время ожидания шлюза истекло";
            default -> "Ошибка " + status.value();
        };
    }
}