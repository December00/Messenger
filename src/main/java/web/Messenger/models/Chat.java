package web.Messenger.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long firstId;

    @Column(nullable = false)
    private Long secondId;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp DESC")
    private List<Message> messages = new ArrayList<>();

    // Конструкторы, геттеры, сеттеры
    public Chat() {

    }

    public Chat(Long firstId, Long secondId) {
        this.firstId = firstId;
        this.secondId = secondId;
    }

    public Long getFirstId(){
        return this.firstId;
    }
    public Long getSecondId(){
        return this.secondId;
    }
    public void setFirstId(Long firstId){
        this.firstId=firstId;
    }
    public void setSecondId(Long secondId){
        this.secondId=secondId;
    }
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}

