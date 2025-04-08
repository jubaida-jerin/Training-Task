package jerin.com.example.expense_tracker.controller;


import jakarta.validation.Valid;
import jerin.com.example.expense_tracker.model.User;
import jerin.com.example.expense_tracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map(user -> {

                    if (!user.getUsername().equals(userDetails.getUsername())) {
                        if (userRepository.existsByUsername(userDetails.getUsername())) {
                            return ResponseEntity.badRequest().body("Username is already taken");
                        }
                        user.setUsername(userDetails.getUsername());
                    }

                    if (!user.getEmail().equals(userDetails.getEmail())) {
                        if (userRepository.existsByEmail(userDetails.getEmail())) {
                            return ResponseEntity.badRequest().body("Email is already in use");
                        }
                        user.setEmail(userDetails.getEmail());
                    }

                    // Update other fields
                    user.setPassword(userDetails.getPassword());

                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}