package com.example.messaging.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.messaging.model.User;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
