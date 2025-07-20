package web.Messenger.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import web.Messenger.models.Chat;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE c.firstId = :userId OR c.secondId = :userId ORDER BY (SELECT MAX(m.timestamp) FROM c.messages m) DESC")
    List<Chat> findAllUserChats(@Param("userId") Long userId);
    boolean existsByFirstIdAndSecondId(Long firstId, Long secondId);
}
