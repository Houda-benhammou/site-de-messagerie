package com.example.messaging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false)
    private String content;

    private String body; // Ajout de body pour correspondre à votre table

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt; // Ajout de sentAt pour correspondre à votre table

    private String subject; // Ajout de subject pour correspondre à votre table

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Constructeur par défaut
    public Message() {
        this.timestamp = LocalDateTime.now(); // Assigner l'horodatage actuel par défaut
        this.sentAt = LocalDateTime.now(); // Assigner l'horodatage d'envoi par défaut
    }

    // Constructeur avec paramètres
    public Message(User sender, User recipient, String content, String body, String subject) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.body = body;
        this.subject = subject;
        this.sentAt = LocalDateTime.now(); // Assigne la date actuelle lors de l'envoi
        this.timestamp = LocalDateTime.now(); // Horodatage du message
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
