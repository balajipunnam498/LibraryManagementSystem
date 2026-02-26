package com.task.controller;

import com.task.dao.LibrarianRepo;
import com.task.model.Librarian;
import com.task.security.KeycloakAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private LibrarianRepo librarianRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Librarian librarian) {

        if (librarianRepo.findByUserName(librarian.getUserName()) != null) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Username already taken"));
        }

        String rawPassword = librarian.getPassword();

        boolean created = keycloakAdminService.createUserInKeycloak(librarian, rawPassword);

        if (!created) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create user in Keycloak"));
        }

        librarian.setPassword(passwordEncoder.encode(rawPassword));
        librarianRepo.save(librarian);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(Map.of(
                "message", "Signup successful!",
                "username", librarian.getUserName(),
                "roles", librarian.getAuthorities()
                    .stream()
                    .map(a -> a.getAuthoritieType())
                    .toList()
            ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Librarian librarian) {

        String token = keycloakAdminService.loginAndGetToken(
            librarian.getUserName(),
            librarian.getPassword()
        );

        if (token == null) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password"));
        }

        return ResponseEntity.ok(Map.of(
            "access_token", token,
            "token_type", "Bearer"
        ));
    }
}