package com.tripmakin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripmakin.model.User;
import com.tripmakin.repository.UserRepository;
import com.tripmakin.security.JwtUtil;
import com.tripmakin.service.RefreshTokenService;
import com.tripmakin.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(com.tripmakin.config.TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private UserService userService;
    @MockBean private RefreshTokenService refreshTokenService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private UserRepository userRepository;

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenNotAuthenticatedForMe() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenRefreshTokenInvalid() throws Exception {
        Mockito.when(refreshTokenService.isValid(anyString())).thenReturn(false);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .cookie(new Cookie("refreshToken", "invalid")))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Refresh token invalid"));
    }

    @Test
    void shouldReturnTestPage() throws Exception {
        mockMvc.perform(get("/api/v1/auth/test"))
            .andExpect(status().isOk())
            .andExpect(content().string("Test page"));
    }

    
}