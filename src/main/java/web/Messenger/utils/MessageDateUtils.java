package web.Messenger.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class MessageDateUtils {

    public String formatMessageTime(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();

        if (timestamp == null) {
            return "";
        }

        if (timestamp.toLocalDate().equals(now.toLocalDate())) {
            return "Сегодня " + timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        else if (timestamp.getYear() == now.getYear()) {
            return timestamp.format(DateTimeFormatter.ofPattern("dd MMM HH:mm"));
        }
        else {
            return timestamp.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
        }
    }

}