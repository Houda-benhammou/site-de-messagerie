package com.example.messaging.service;

import com.example.messaging.model.Message;
import com.example.messaging.model.User;
import com.example.messaging.repository.MessageRepository;
import com.example.messaging.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;  // Injecting SimpMessagingTemplate to send STOMP messages

    // Method to send a message
    public Message sendMessage(Message message) {
        try {
            // Validate sender and recipient
            User sender = findUserByUsername(message.getSender().getUsername(), "sender");
            User recipient = findUserByUsername(message.getRecipient().getUsername(), "recipient");

            message.setSender(sender);
            message.setRecipient(recipient);

            // Save the message in the database
            Message savedMessage = messageRepository.save(message);

            // Send the message to the recipient via WebSocket
            messagingTemplate.convertAndSendToUser(recipient.getUsername(), "/queue/messages", savedMessage);

            return savedMessage;
        } catch (Exception e) {
            throw new RuntimeException("Error sending message: " + e.getMessage(), e); // Add more detailed error handling if needed
        }
    }

    // Helper method to find a user by username with appropriate error handling
    private User findUserByUsername(String username, String role) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid " + role + " username: " + username));
    }

    // Method to get received messages by recipient's username
    public List<Message> getMessagesByRecipientUsername(String recipientUsername) {
        User recipient = findUserByUsername(recipientUsername, "recipient");
        return messageRepository.findByRecipient(recipient);
    }

    // Method to get sent messages by sender's username
    public List<Message> getMessagesBySenderUsername(String senderUsername) {
        User sender = findUserByUsername(senderUsername, "sender");
        return messageRepository.findBySender(sender);
    }

    // Method to delete a message by its ID
    public boolean deleteMessageById(Long messageId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent()) {
            messageRepository.deleteById(messageId);
            return true;
        }
        return false;
    }

    // Method to delete all messages in a conversation between two users
    public boolean deleteConversation(String senderUsername, String recipientUsername) {
        List<Message> messages = messageRepository.findBySenderUsernameAndRecipientUsernameOrRecipientUsernameAndSenderUsername(
                senderUsername, recipientUsername, senderUsername, recipientUsername);

        if (!messages.isEmpty()) {
            messageRepository.deleteAll(messages);
            return true;
        }
        return false;
    }
}
