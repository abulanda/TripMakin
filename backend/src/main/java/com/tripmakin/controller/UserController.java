package com.tripmakin.controller;

import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.model.User;
import com.tripmakin.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

@Tag(name = "Users", description = "Endpoints for managing users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "userId,asc") String[] sort
    ) {
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get a user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(404).build();
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Create a new user", description = "Add a new user to the system")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User successfully created"),
        @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User newUser) {
        return ResponseEntity.status(201).body(userService.createUser(newUser));
    }

    @Operation(summary = "Update an existing user", description = "Update the details of an existing user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User successfully updated"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
        @PathVariable Integer id,
        @RequestPart("user") @Valid User updatedUser,
        @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String fileName = saveProfilePicture(profilePicture, id);
            updatedUser.setProfilePicture(fileName);
        } else {
            updatedUser.setProfilePicture(user.getProfilePicture());
        }
        return ResponseEntity.ok(userService.updateUser(id, updatedUser));
    }

    @Operation(summary = "Delete a user", description = "Remove a user from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User successfully deleted"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    @Operation(summary = "Get user by email", description = "Retrieve a user by their email address")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return ResponseEntity.ok(user);
    }

    private String saveProfilePicture(MultipartFile file, Integer userId) {
        String uploadDir = "uploads/profile_pictures";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String fileName = "user_" + userId + extension;
        File destination = new File(dir, fileName);

        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas zapisu pliku profilowego", e);
        }

        return "/" + uploadDir + "/" + fileName;
    }
}
