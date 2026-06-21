package com.sentineliq.backend;

import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Backend is running 🚀";
    }
}