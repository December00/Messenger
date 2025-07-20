package web.Messenger.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import web.Messenger.models.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByChatIdOrderByTimestampDesc(Long chatId, Pageable pageable);

}