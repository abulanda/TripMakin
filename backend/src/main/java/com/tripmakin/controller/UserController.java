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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Tag(name = "Users", description = "Endpoints for managing users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
        summary = "Get all users",
        description = "Retrieve a paginated list of all users",
        parameters = {
            @Parameter(name = "page", description = "Page number (zero-based)", example = "0"),
            @Parameter(name = "size", description = "Page size", example = "10"),
            @Parameter(name = "sort", description = "Sort format: [property,asc|desc]", example = "[\"userId\",\"asc\"]")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
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

    @Operation(
        summary = "Get a user by ID",
        description = "Retrieve a specific user by their ID",
        parameters = @Parameter(name = "id", description = "ID of the user", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the user",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"User not found\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Create a new user",
        description = "Add a new user to the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Validation failed\"}")))
    })
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User newUser) {
        return ResponseEntity.status(201).body(userService.createUser(newUser));
    }

    @Operation(
        summary = "Update an existing user",
        description = "Update the details of an existing user",
        parameters = @Parameter(name = "id", description = "ID of the user", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"User not found\"}"))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Validation failed\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer id,
            @RequestBody User updatedUser
    ) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Delete a user",
        description = "Remove a user from the system",
        parameters = @Parameter(name = "id", description = "ID of the user", required = true, example = "1")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User successfully deleted",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"message\": \"User deleted\"}"))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"User not found\"}")))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Integer id) {
        userService.getUserById(id);
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    @Operation(
        summary = "Get user by email",
        description = "Retrieve a user by their email address",
        parameters = @Parameter(name = "email", description = "Email address of the user", required = true, example = "user@example.com")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the user",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"User not found\"}")))
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
