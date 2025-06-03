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
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyInt;
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
        User user = new User();
        user.setUserId(1);
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail("jan@kowalski.pl");

        when(userService.updateUser(anyInt(), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void shouldReturn404WhenUpdateUserNotFound() throws Exception {
        User user = new User();
        user.setUserId(1);
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail("jan@kowalski.pl");

        when(userService.updateUser(anyInt(), any(User.class)))
            .thenThrow(new com.tripmakin.exception.ResourceNotFoundException("User not found"));

        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
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

    @Test
    void shouldReturnUserByEmail() throws Exception {
        Mockito.when(userService.findByEmail("anna@example.com")).thenReturn(sample(1, "Anna", "Nowak", "anna@example.com"));

        mockMvc.perform(get("/api/v1/users/email/anna@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Anna"));
    }

    @Test
    void shouldReturn404WhenUserByEmailNotFound() throws Exception {
        Mockito.when(userService.findByEmail("nieistnieje@example.com")).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/email/nieistnieje@example.com"))
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
        u.setPhoneNumber("123456789");
        u.setBio("Przykładowy opis");
        u.setLastLoginAt(null);
        return u;
    }
}