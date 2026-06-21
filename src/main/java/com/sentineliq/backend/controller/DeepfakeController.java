package com.sentineliq.backend.controller;

import com.sentineliq.backend.PhishingResponse;
import com.sentineliq.backend.service.DeepfakeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/deepfake")
public class DeepfakeController {

    @Autowired
    private DeepfakeService service;

    @PostMapping("/analyze")
    public PhishingResponse analyze(@RequestParam("file") MultipartFile file) {
        return service.analyzeFile(file);
    }
}