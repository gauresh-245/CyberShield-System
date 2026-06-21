package com.sentineliq.backend.controller;

import com.sentineliq.backend.PhishingResponse;
import com.sentineliq.backend.dto.UserActivityRequest;
import com.sentineliq.backend.service.AnomalyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/anomaly")
public class AnomalyController {

    @Autowired
    private AnomalyService service;

    @PostMapping("/analyze")
    public PhishingResponse analyze(@RequestBody UserActivityRequest request) {
        return service.analyze(request);
    }
}