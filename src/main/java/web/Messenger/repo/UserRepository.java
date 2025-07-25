package web.Messenger.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import web.Messenger.models.User;

import java.util.Optional;
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
}
