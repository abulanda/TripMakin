package com.tripmakin.controller;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @GetMapping("/test")
    public String login() {
        return "Test admin page";
    }

}
