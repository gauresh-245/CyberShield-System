package com.sentineliq.backend.controller;

import com.sentineliq.backend.PhishingResponse;
import com.sentineliq.backend.service.PromptInjectionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prompt")
public class PromptInjectionController {

    @Autowired
    private PromptInjectionService service;

    @PostMapping("/analyze")
    public PhishingResponse analyze(@RequestBody String input) {
        return service.analyzePrompt(input);
    }
}