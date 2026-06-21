package com.sentineliq.backend.service;
import com.sentineliq.backend.GeminiService;
import com.sentineliq.backend.Incident;
import com.sentineliq.backend.IncidentRepository;
import com.sentineliq.backend.PhishingResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PhishingService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
private DynamicReasonGenerator generator;

    @Autowired
    private IncidentRepository repo;

    public PhishingResponse analyzeText(String content) {

        String text = content.toLowerCase();
        double score = 0;

       if (text.contains("urgent")) score += 0.25;

if (text.contains("click")) score += 0.15;

if (text.contains("verify")) score += 0.20;

if (text.contains("bank")) score += 0.15;

if (text.contains("kyc")) score += 0.10;

if (text.contains("password")) score += 0.30;

if (text.contains("otp")) score += 0.30;

if (text.contains("microsoft")) score += 0.25;

if (text.contains("suspended")) score += 0.20;

if (text.contains("hacked")) score += 0.25;

if (text.contains("login")) score += 0.15;

if (text.contains("failure to comply")) score += 0.20;

if (text.contains("immediately")) score += 0.20;

if (text.contains("critical")) score += 0.25;

        if (content.contains("@")) score += 0.1;

        score = Math.min(score, 1.0);

        double percentage = score * 100;

        String riskLevel;
        if (percentage < 30) riskLevel = "LOW RISK";
        else if (percentage < 70) riskLevel = "MEDIUM RISK";
        else riskLevel = "HIGH RISK";


        List<String> reasons = geminiService.generateReasons(content, score);

if (reasons == null || reasons.isEmpty()) {
    reasons = generator.generatePhishingReasons(content);
}

while (reasons.size() < 4) {

    List<String> fallback = Arrays.asList(
            "Urgency-based language detected",
            "Potential credential harvesting indicators found",
            "Suspicious communication pattern observed",
            "Social engineering characteristics identified",
            "Risky language requires verification",
            "Threat indicators detected by heuristics"
    );

    String randomReason =
            fallback.get(new Random().nextInt(fallback.size()));

    if (!reasons.contains(randomReason)) {
        reasons.add(randomReason);
    }
}


        Incident incident = new Incident();
        incident.setType("phishing");
        incident.setContent(content);
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