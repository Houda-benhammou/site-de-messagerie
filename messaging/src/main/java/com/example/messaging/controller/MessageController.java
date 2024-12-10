package com.example.messaging.controller;

import com.example.messaging.model.Message;
import com.example.messaging.service.MessageService;
import com.example.messaging.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from the frontend
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @SuppressWarnings("unused")
    @Autowired
    private UserService userService;

    // Send a message
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        Message savedMessage = messageService.sendMessage(message);  // Send the message through the service
        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipient().getUsername(), "/topic/messages", savedMessage);  // Send message via WebSocket
        return new ResponseEntity<>(savedMessage, HttpStatus.OK);  // Return the saved message
    }

    // Delete a message
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        boolean isDeleted = messageService.deleteMessageById(messageId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get received messages for a user
    @GetMapping("/received/{username}")
    public List<Message> getReceivedMessages(@PathVariable String username) {
        return messageService.getMessagesByRecipientUsername(username);
    }

    // Get sent messages for a user
    @GetMapping("/sent/{username}")
    public List<Message> getSentMessages(@PathVariable String username) {
        return messageService.getMessagesBySenderUsername(username);
    }
}
