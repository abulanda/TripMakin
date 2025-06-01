package com.tripmakin.service;

import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.model.User;
import com.tripmakin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tripmakin.messaging.MessageProducer;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MessageProducer messageProducer;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User createUser(User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        if (newUser.getRoles() == null || newUser.getRoles().isEmpty()) {
            newUser.setRoles(Set.of("USER"));
        }
        User savedUser = userRepository.save(newUser);
        messageProducer.sendMessage("Utworzono nowego użytkownika: " + savedUser.getEmail());
        return savedUser;
    }

    public User updateUser(Integer id, User updatedUser) {
        User existingUser = getUserById(id);
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setNickname(updatedUser.getNickname());
        existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()
            && !updatedUser.getPassword().equals(existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        existingUser.setProfilePicture(updatedUser.getProfilePicture());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setBio(updatedUser.getBio());
        existingUser.setLastLoginAt(updatedUser.getLastLoginAt());
        existingUser.setIsActive(updatedUser.getIsActive());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Integer id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User findByEmail(String email) {
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
}
}
