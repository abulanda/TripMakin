package com.tripmakin.controller;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin", description = "Endpoints for admin operations")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    
    @Operation(
        summary = "Test admin endpoint",
        description = "Simple test endpoint for admin role"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Test admin page"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient admin privileges")
    })
    @GetMapping("/test")
    public String login() {
        return "Test admin page";
    }

}
