package web.Messenger.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Конструкторы, геттеры, сеттеры
    public Message() {}

    public Message(Chat chat, String senderId, String content, LocalDateTime timestamp) {
        this.chat = chat;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Добавьте геттеры и сеттеры для всех полей
}