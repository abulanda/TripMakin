package com.tripmakin.controller;

import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.model.User;
import com.tripmakin.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Users", description = "Endpoints for managing users")
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @Operation(summary = "Get a user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the user", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found", 
                     content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        return user
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Operation(summary = "Create a new user", description = "Add a new user to the system")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User successfully created", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", 
                     content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User newUser) {
        User savedUser = userRepository.save(newUser);
        return ResponseEntity.status(201).body(savedUser);
    }

    @Operation(summary = "Update an existing user", description = "Update the details of an existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User successfully updated", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Validation failed", 
                     content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setNickname(updatedUser.getNickname());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            user.setProfilePicture(updatedUser.getProfilePicture());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            user.setBio(updatedUser.getBio());
            user.setLastLoginAt(updatedUser.getLastLoginAt());
            user.setIsActive(updatedUser.getIsActive());
            return ResponseEntity.ok(userRepository.save(user));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "User not found", "status", 404));
        }
    }

    @Operation(summary = "Delete a user", description = "Remove a user from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User successfully deleted", 
                     content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "User not found", 
                     content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted"));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "User not found", "status", 404));
        }
    }
}
