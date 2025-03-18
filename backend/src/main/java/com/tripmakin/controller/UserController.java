package com.tripmakin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Map<Integer, Map<String, String>> users = new HashMap<>();

    static {
        users.put(1, Map.of("id", "1", "name", "Jan Kowalski", "email", "jan@example.com"));
        users.put(2, Map.of("id", "2", "name", "Anna Nowak", "email", "anna@example.com"));
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return ResponseEntity.ok(users.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        if (!users.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(users.get(id));
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody Map<String, String> newUser) {
            int newId = users.size() + 1;
            users.put(newId, Map.of(
                "id", String.valueOf(newId),
                "name", newUser.get("name"),
                "email", newUser.get("email")
        ));
    return ResponseEntity.status(201).body(users.get(newId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable int id, @RequestBody Map<String, String> updatedUser) {
        if (!users.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        users.put(id, Map.of(
            "id", String.valueOf(id),
            "name", updatedUser.get("name"),
            "email", updatedUser.get("email")
        ));
        return ResponseEntity.ok(users.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        if (!users.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        users.remove(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

}
