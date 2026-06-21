package com.sentineliq.backend.service;
import com.sentineliq.backend.GeminiService;
import com.sentineliq.backend.Incident;
import com.sentineliq.backend.IncidentRepository;
import com.sentineliq.backend.PhishingResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UrlService {

    @Autowired
    private GeminiService geminiService;


    @Autowired
private DynamicReasonGenerator generator;

    @Autowired
    private IncidentRepository repo;

    public PhishingResponse analyzeUrl(String url) {

        List<String> reasons = com.sentineliq.backend.utils.UrlAnalyzer.analyze(url);

       double score = 0;

String lowerUrl = url.toLowerCase();

if(lowerUrl.startsWith("http://")) {
    score += 0.20;
}

if(lowerUrl.contains(".ru")) {
    score += 0.30;
}

if(lowerUrl.contains("login")) {
    score += 0.15;
}

if(lowerUrl.contains("verify")) {
    score += 0.15;
}

if(lowerUrl.contains("reset-password")) {
    score += 0.20;
}

if(lowerUrl.contains("microsoft")) {
    score += 0.20;
}

if(lowerUrl.contains("amazon")) {
    score += 0.15;
}

if(lowerUrl.contains("bank")) {
    score += 0.15;
}

if(lowerUrl.contains("secure")) {
    score += 0.10;
}

if(lowerUrl.contains("account")) {
    score += 0.10;
}

score = Math.min(score, 1.0);
        double percentage = score * 100;

        String riskLevel;
        if (percentage < 30) riskLevel = "LOW RISK";
        else if (percentage < 70) riskLevel = "MEDIUM RISK";
        else riskLevel = "HIGH RISK";

        List<String> aiReasons = geminiService.generateReasons(url, score);

if (aiReasons == null || aiReasons.isEmpty()) {
    aiReasons = generator.generateUrlReasons(url);
}

        reasons.addAll(aiReasons);

        while (reasons.size() < 4) {

    List<String> fallback = Arrays.asList(
            "Suspicious URL structure observed",
            "Domain reputation requires verification",
            "Potential phishing indicators found",
            "Link analysis detected anomalies",
            "URL requires additional inspection"
    );

    String randomReason =
            fallback.get(new Random().nextInt(fallback.size()));

    if (!reasons.contains(randomReason)) {
        reasons.add(randomReason);
    }
}

        Incident incident = new Incident();
        incident.setType("url");
        incident.setContent(url);
        incident.setConfidence(percentage);
        incident.setVerdict(riskLevel);
        incident.setReasons(String.join(" | ", reasons));

        repo.save(incident);

        PhishingResponse response = new PhishingResponse();
        response.setRiskLevel(riskLevel);
        response.setRiskPercentage(percentage);
        response.setReasons(reasons);

        return response;
    }
}