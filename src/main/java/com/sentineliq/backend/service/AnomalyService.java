package com.sentineliq.backend.service;

import com.sentineliq.backend.*;
import com.sentineliq.backend.dto.UserActivityRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnomalyService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private IncidentRepository repo;

    public PhishingResponse analyze(UserActivityRequest req) {

        double score = 0;

        // 🔥 RULE 1: Too many login attempts
        if (req.getLoginAttempts() > 5) score += 0.3;

        // 🔥 RULE 2: Night login
        int hour = Integer.parseInt(req.getTime().split(":")[0]);
        if (hour < 5 || hour > 23) score += 0.2;

        // 🔥 RULE 3: Suspicious location keywords
        if (req.getLocation().toLowerCase().contains("unknown")) score += 0.2;

        // 🔥 RULE 4: Suspicious IP
        if (req.getIp().startsWith("192.168")) score += 0.1;

        // 🔥 RULE 5: Unknown device
        if (req.getDevice().toLowerCase().contains("unknown")) score += 0.2;

        // 🔥 RANDOMNESS (keep system dynamic)
        score = Math.min(score + (Math.random() * 0.2), 1.0);

        double percentage = score * 100;

        String riskLevel;
        if (percentage < 30) riskLevel = "LOW RISK";
        else if (percentage < 70) riskLevel = "MEDIUM RISK";
        else riskLevel = "HIGH RISK";

        // 🤖 AI reasoning
        String input = "User Activity:\n"
                + "User: " + req.getUserId() + "\n"
                + "Location: " + req.getLocation() + "\n"
                + "Device: " + req.getDevice() + "\n"
                + "IP: " + req.getIp() + "\n"
                + "Attempts: " + req.getLoginAttempts() + "\n"
                + "Time: " + req.getTime();

        List<String> reasons = geminiService.generateReasons(
                "Analyze this user behavior for anomalies:\n" + input,
                score
        );

        // 💾 Save
        Incident incident = new Incident();
        incident.setType("anomaly");
        incident.setContent(input);
        incident.setConfidence(percentage);
        incident.setVerdict(riskLevel);
        incident.setReasons(String.join(" | ", reasons));

        repo.save(incident);

        // 📤 Response
        PhishingResponse response = new PhishingResponse();
        response.setRiskLevel(riskLevel);
        response.setRiskPercentage(percentage);
        response.setReasons(reasons);

        return response;
    }
}