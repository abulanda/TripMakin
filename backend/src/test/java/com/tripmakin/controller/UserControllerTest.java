package com.tripmakin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmakin.model.User;
import com.tripmakin.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UserRepository userRepository;

    @Test
    void getUsers_ok() throws Exception {
        User u1 = sample(1, "Barbara", "Flaming", "barbara.flaming@example.com");
        User u2 = sample(2, "Tomasz", "Hamak", "tomasz.hamak@example.com");
        Mockito.when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/users"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].firstName").value("Barbara"))
               .andExpect(jsonPath("$[1].firstName").value("Tomasz"));
    }

    @Test
    void getUserById_ok() throws Exception {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(sample(1)));

        mockMvc.perform(get("/api/users/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("Barbara"));
    }

    @Test
    void getUserById_notFound() throws Exception {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/1"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("User not found"))
               .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createUser_created() throws Exception {
        User body = sample(null);
        Mockito.when(userRepository.save(any(User.class)))
               .thenAnswer(inv -> { User u = inv.getArgument(0); u.setUserId(3); return u; });

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.firstName").value("Barbara"));
    }

    @Test
    void createUser_unprocessable() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void updateUser_ok() throws Exception {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(sample(1)));
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User patch = sample(null);
        patch.setFirstName("Tomasz");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("Tomasz"));
    }

    @Test
    void updateUser_notFound() throws Exception {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample(null))))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("User not found"))
               .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteUser_ok() throws Exception {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(sample(1)));

        mockMvc.perform(delete("/api/users/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("User deleted"));
    }

    @Test
    void deleteUser_notFound() throws Exception {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/1"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("User not found"))
               .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createUser_badRequest() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lastName\":\"Flaming\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_badRequest() throws Exception {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(sample(1)));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lastName\":\"Flaming\"}"))
                .andExpect(status().isBadRequest());
    }

    private User sample(Integer id) { 
        return sample(id, "Barbara", "Flaming", "barbara.flaming@example.com"); 
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