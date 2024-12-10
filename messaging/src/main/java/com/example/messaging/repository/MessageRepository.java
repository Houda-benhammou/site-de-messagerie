

package com.example.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.messaging.model.Message;
import com.example.messaging.model.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipient(User recipient);
    List<Message> findBySender(User sender);
    List<Message> findBySenderUsernameAndRecipientUsernameOrRecipientUsernameAndSenderUsername(
        String senderUsername, String recipientUsername, String reversedSenderUsername, String reversedRecipientUsername);
}