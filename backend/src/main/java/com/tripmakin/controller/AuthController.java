package com.tripmakin.controller;

import com.tripmakin.model.User;
import com.tripmakin.service.UserService;
import com.tripmakin.repository.UserRepository;
import java.time.LocalDateTime;
import com.tripmakin.service.RefreshTokenService;
import com.tripmakin.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "Endpoints for authentication and user session management")
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    public AuthController(
        AuthenticationManager authenticationManager,
        UserService userService,
        RefreshTokenService refreshTokenService,
        JwtUtil jwtUtil
    , UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Operation(
        summary = "Authenticate user and return JWT",
        description = "Authenticate user credentials and return JWT and refresh token cookies"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"userId\": 1}"))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\": \"Invalid credentials\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String token = jwtUtil.generateToken(authentication.getName(), roles);

            User user = userService.findByEmail(authentication.getName());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
            }

            String refreshToken = refreshTokenService.generateRefreshToken(user);

            Cookie jwtCookie = new Cookie("jwtToken", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);
            jwtCookie.setSecure(false);
            response.addCookie(jwtCookie);

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60);
            refreshCookie.setSecure(false);
            response.addCookie(refreshCookie);

            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                "userId", user.getUserId()
            ));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    @Operation(
        summary = "Logout user",
        description = "Clear authentication cookies and logout user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully logged out")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwtToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Test endpoint",
        description = "Simple test endpoint for authentication"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Test page")
    })
    @GetMapping("/test")
    public String test() {
        return "Test page";
    }

    @Operation(
        summary = "Get current user info",
        description = "Get information about the currently authenticated user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user info",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"username\": \"user@example.com\", \"roles\": [\"ROLE_USER\"]}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(Map.of(
            "username", authentication.getName(),
            "roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        ));
    }

    @Operation(
        summary = "Refresh JWT token",
        description = "Refresh JWT and refresh token using the refresh token cookie"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully refreshed tokens"),
        @ApiResponse(responseCode = "401", description = "Refresh token invalid",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "Refresh token invalid")))
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null || !refreshTokenService.isValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token invalid");
        }
        String username = refreshTokenService.getUsername(refreshToken);
        User user = userService.findByEmail(username);
        List<String> roles = user.getRoles().stream().toList();
        String newJwt = JwtUtil.generateToken(user.getEmail(), roles);
        String newRefresh = refreshTokenService.generateRefreshToken(user);

        Cookie jwtCookie = new Cookie("jwtToken", newJwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);

        Cookie refreshCookie = new Cookie("refreshToken", newRefresh);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        return ResponseEntity.ok().build();
    }
}

class AuthRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
