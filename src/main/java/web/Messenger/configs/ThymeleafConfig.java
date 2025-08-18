package web.Messenger.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import web.Messenger.utils.DateUtils;
import web.Messenger.utils.MessageDateUtils;

@Configuration
public class ThymeleafConfig {
    @Bean
    public DateUtils dateUtils() {
        return new DateUtils();
    }
    @Bean
    public MessageDateUtils messageDateUtils() {
        return new MessageDateUtils();
    }
}