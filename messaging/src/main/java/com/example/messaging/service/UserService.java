package com.example.messaging.service;

import com.example.messaging.model.User;
import com.example.messaging.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Créer un nouvel utilisateur
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Trouver un utilisateur par son nom d'utilisateur
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Récupérer tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Supprimer un utilisateur
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // Connexion d'un utilisateur
    public User loginUser(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                user.setLastLogin(LocalDateTime.now());
                user.setOnline(true);
                return userRepository.save(user);
            }
        }
        return null;
    }

    // Déconnexion de l'utilisateur
    public User logoutUser(String username) throws Exception {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastLogin(LocalDateTime.now());
            user.setOnline(false);
            return userRepository.save(user);
        } else {
            throw new Exception("Utilisateur avec le nom d'utilisateur '" + username + "' non trouvé");
        }
    }

    // Mise à jour de l'utilisateur
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // Trouver un utilisateur par son ID
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }
}
