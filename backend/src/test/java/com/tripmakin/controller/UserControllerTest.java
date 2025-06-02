package com.tripmakin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmakin.model.User;
import com.tripmakin.service.UserService;
import com.tripmakin.exception.ResourceNotFoundException;
import com.tripmakin.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UserService userService;

    @Test
    void shouldReturnAllUsers() throws Exception {
        User u1 = sample(1, "Anna", "Nowak", "anna@example.com");
        User u2 = sample(2, "Jan", "Kowalski", "jan@example.com");
        Mockito.when(userService.getAllUsers(Mockito.any())).thenReturn(new PageImpl<>(List.of(u1, u2), PageRequest.of(0, 10), 2));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Anna"))
                .andExpect(jsonPath("$.content[1].firstName").value("Jan"));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        Mockito.when(userService.getUserById(1)).thenReturn(sample(1, "Anna", "Nowak", "anna@example.com"));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Anna"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        Mockito.when(userService.getUserById(1)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    void shouldCreateUser() throws Exception {
        User body = sample(null, "Anna", "Nowak", "anna@example.com");
        Mockito.when(userService.createUser(any(User.class)))
                .thenAnswer(inv -> { User u = inv.getArgument(0); u.setUserId(3); return u; });

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Anna"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateUserWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        Mockito.when(userService.getUserById(1)).thenReturn(sample(1, "Anna", "Nowak", "anna@example.com"));
        Mockito.when(userService.updateUser(Mockito.eq(1), Mockito.any(User.class)))
                .thenAnswer(inv -> inv.getArgument(1));

        User patch = sample(null, "Jan", "Nowak", "jan@example.com");

        mockMvc.perform(multipart("/api/v1/users/1")
                .file(new MockMultipartFile("user", "", "application/json", objectMapper.writeValueAsBytes(patch)))
                .file(new MockMultipartFile("profilePicture", new byte[0]))
                .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jan"));
    }

    @Test
    void shouldReturn404WhenUpdateUserNotFound() throws Exception {
        Mockito.when(userService.getUserById(1)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(multipart("/api/v1/users/1")
                .file(new MockMultipartFile("user", "", "application/json", objectMapper.writeValueAsBytes(sample(null, "Anna", "Nowak", "anna@example.com"))))
                .file(new MockMultipartFile("profilePicture", new byte[0]))
                .with(request -> { request.setMethod("PUT"); return request; }))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        Mockito.when(userService.getUserById(1)).thenReturn(sample(1, "Anna", "Nowak", "anna@example.com"));

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted"));
    }

    @Test
    void shouldReturn404WhenDeleteUserNotFound() throws Exception {
        Mockito.when(userService.getUserById(1)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    private User sample(Integer id, String fn, String ln, String email) {
        User u = new User();
        u.setUserId(id);
        u.setFirstName(fn);
        u.setLastName(ln);
        u.setEmail(email);
        u.setPassword("haslo123");
        u.setIsActive(true);
        u.setProfilePicture("zdjecie.jpg");
        u.setPhoneNumber("123456789");
        u.setBio("Przykładowy opis");
        u.setLastLoginAt(null);
        return u;
    }
}