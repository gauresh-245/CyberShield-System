package com.sentineliq.backend.controller;

import com.sentineliq.backend.dto.RegisterRequest;
import com.sentineliq.backend.entity.User;
import com.sentineliq.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userRepository.save(user);

        return "User Registered Successfully";
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User user) {

    User existingUser = userRepository.findByEmail(user.getEmail());

    if(existingUser == null) {

        return ResponseEntity.badRequest().body("User Not Found");
    }

    if(!existingUser.getPassword().equals(user.getPassword())) {

        return ResponseEntity.badRequest().body("Invalid Password");
    }

    return ResponseEntity.ok(existingUser);
}
}