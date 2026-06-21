package com.sentineliq.backend.controller;

import com.sentineliq.backend.PhishingResponse;
import com.sentineliq.backend.service.PhishingService;
import com.sentineliq.backend.service.UrlService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.multipart.MultipartFile;
import com.sentineliq.backend.service.FileService;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ThreatController {

    @Autowired
    private PhishingService phishingService;

    @Autowired
    private UrlService urlService;

    // ✅ TEXT PHISHING
    @PostMapping("/phishing")
    public PhishingResponse analyze(@RequestBody String content) {
        return phishingService.analyzeText(content);
    }

    // ✅ URL PHISHING
    @PostMapping("/url")
    public PhishingResponse analyzeUrl(@RequestBody String url) {
        return urlService.analyzeUrl(url);
    }


    @Autowired
    private FileService fileService;

    @PostMapping("/file")
    public PhishingResponse analyzeFile(@RequestParam("file") MultipartFile file) {
          return fileService.analyzeFile(file);
    }
}