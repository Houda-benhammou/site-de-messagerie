package com.example.messaging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nom;
    private String prenom;

    @Column(name = "numero_telephone", length = 20)
    private String numeroTelephone;

    @Column(name = "photo_de_profile")
    private String photoDeProfile;

    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_online")
    private boolean isOnline; // New field for online status

    // Enum for sexe field
    public enum Sexe {
        M, F, OTHER
    }

    // Default constructor required by JPA
    public User() {
    }

    // Constructor with parameters
    public User(Long id, String username, String email, String password, String nom, String prenom, String numeroTelephone, String photoDeProfile, Sexe sexe, LocalDateTime lastLogin, boolean isOnline) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.numeroTelephone = numeroTelephone;
        this.photoDeProfile = photoDeProfile;
        this.sexe = sexe;
        this.lastLogin = lastLogin;
        this.isOnline = isOnline;
    }

    // Getters and setters for all fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getPhotoDeProfile() {
        return photoDeProfile;
    }

    public void setPhotoDeProfile(String photoDeProfile) {
        this.photoDeProfile = photoDeProfile;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
