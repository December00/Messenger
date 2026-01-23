package web.Messenger.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import web.Messenger.models.Session;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByToken(String token);
    @Query("SELECT s FROM Session s WHERE s.token = :token AND s.expiresAt > :now")
    Optional<Session> findValidSession(@Param("token") String token, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM Session s WHERE s.expiresAt <= :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM Session s WHERE s.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    Optional<Session> findByUserId(Long userId);
}
