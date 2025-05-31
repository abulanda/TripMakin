package com.tripmakin.controller;

import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @GetMapping("/test")
    public String login() {
        return "Test admin page";
    }

}
