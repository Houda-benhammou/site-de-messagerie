package com.example.messaging.controller;

import com.example.messaging.model.User;
import com.example.messaging.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000") // Autoriser les requêtes CORS depuis le front-end
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;  // Injecter SimpMessagingTemplate pour envoyer des messages STOMP

    // Créer un nouvel utilisateur
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // Récupérer tous les utilisateurs
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // Connexion d'un utilisateur
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody User user) {
        User loggedInUser = userService.loginUser(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            loggedInUser.setOnline(true);  // Marquer l'utilisateur comme en ligne
            userService.updateUser(loggedInUser);  // Sauvegarder l'état de l'utilisateur
            messagingTemplate.convertAndSend("/topic/user-status", loggedInUser);  // Envoyer un message STOMP pour mettre à jour l'état de l'utilisateur
            return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Renvoie 401 si l'authentification échoue
        }
    }

    // Déconnexion d'un utilisateur
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        Optional<User> optionalUser = userService.findUserByUsername(username); // Utilisation de Optional
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setOnline(false);  // Marquer l'utilisateur comme hors ligne
            userService.updateUser(user);  // Sauvegarder l'état de l'utilisateur
            messagingTemplate.convertAndSend("/topic/user-status", user);  // Envoyer un message STOMP pour mettre à jour l'état de l'utilisateur
            return ResponseEntity.ok("User logged out successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    // Récupérer un utilisateur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userService.findUserById(id);
        if (optionalUser.isPresent()) {
            return ResponseEntity.ok(optionalUser.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

// Mettre à jour un utilisateur, y compris la photo de profil
@PutMapping("/{id}")
public ResponseEntity<?> updateUser(
        @PathVariable Long id,
        @RequestParam(value = "username", required = false) String username,
        @RequestParam(value = "password", required = false) String password,
        @RequestParam(value = "email", required = false) String email,
        @RequestParam(value = "online", required = false) Boolean online,
        @RequestParam(value = "photo", required = false) MultipartFile photo,
        @RequestParam(value = "nom", required = false) String nom,
        @RequestParam(value = "prenom", required = false) String prenom,
        @RequestParam(value = "numeroTelephone", required = false) String numeroTelephone) {

    Optional<User> optionalUser = userService.findUserById(id); // Récupérer l'utilisateur par son ID
    if (optionalUser.isPresent()) {
        User existingUser = optionalUser.get();

        // Mise à jour des informations textuelles de l'utilisateur
        if (username != null) existingUser.setUsername(username);
        if (password != null) existingUser.setPassword(password);
        if (email != null) existingUser.setEmail(email);
        if (online != null) existingUser.setOnline(online);
        if (nom != null) existingUser.setNom(nom);
        if (prenom != null) existingUser.setPrenom(prenom);
        if (numeroTelephone != null) existingUser.setNumeroTelephone(numeroTelephone);

        // Gestion de la photo de profil
        if (photo != null && !photo.isEmpty()) {
            try {
                // Déterminer le répertoire de stockage des photos
                String uploadDir = System.getProperty("user.home") + "/uploads/"; // Répertoire utilisateur pour plus de sécurité
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs(); // Créer le répertoire s'il n'existe pas
                }

                // Générer un chemin unique pour le fichier
                String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                photo.transferTo(filePath.toFile()); // Enregistrer le fichier sur le disque

                // Mettre à jour le champ photoDeProfile avec le chemin relatif
                existingUser.setPhotoDeProfile(fileName); // Enregistrer uniquement le nom du fichier (relatif)
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to upload photo: " + e.getMessage());
            }
        }

        // Sauvegarder l'utilisateur mis à jour
        User savedUser = userService.updateUser(existingUser);
        return ResponseEntity.ok(savedUser); // Retourner l'utilisateur mis à jour
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
}

// Serveur des fichiers d'images
@GetMapping("/uploads/{filename:.+}")
@ResponseBody
public ResponseEntity<?> serveFile(@PathVariable String filename) {
    String uploadDir = System.getProperty("user.home") + "/uploads/"; // Répertoire utilisateur
    Path filePath = Paths.get(uploadDir).resolve(filename);

    // Vérifier si le fichier existe
    if (Files.exists(filePath)) {
        try {
            // Charger le fichier comme un flux d'entrée
            File file = filePath.toFile();
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamResource resource = new InputStreamResource(fileInputStream);

            // Déterminer le type de contenu basé sur l'extension du fichier (image/jpeg ou image/png, etc.)
            String contentType = Files.probeContentType(filePath);

            // Retourner le fichier dans la réponse
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType)) // Content-Type dynamique
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to read file: " + e.getMessage());
        }
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
    }
}

}
