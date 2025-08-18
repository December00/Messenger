package web.Messenger.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String formatMessageTime(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.toLocalDate().atStartOfDay();
        LocalDateTime yesterday = today.minusDays(1);

        if (timestamp.isAfter(today)) {
            return DateTimeFormatter.ofPattern("HH:mm").format(timestamp);
        } else if (timestamp.isAfter(yesterday)) {
            return "Вчера";
        } else if (timestamp.isAfter(today.minusDays(7))) {
            return DateTimeFormatter.ofPattern("EEE").format(timestamp);
        } else {
            return DateTimeFormatter.ofPattern("dd MMM").format(timestamp);
        }
    }
}