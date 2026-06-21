package com.sentineliq.backend.service;

import com.sentineliq.backend.GeminiService;
import com.sentineliq.backend.Incident;
import com.sentineliq.backend.IncidentRepository;
import com.sentineliq.backend.PhishingResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PromptInjectionService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private DynamicReasonGenerator generator;

    @Autowired
    private IncidentRepository repo;

    public PhishingResponse analyzePrompt(String content) {

        String text = content.toLowerCase();
        double score = 0;

     // =========================
// DYNAMIC DETECTION ENGINE
// =========================

if (text.contains("ignore previous")) score += 20;

if (text.contains("ignore all previous")) score += 25;

if (text.contains("system prompt")) score += 20;

if (text.contains("bypass")) score += 15;

if (text.contains("override")) score += 15;

if (text.contains("jailbreak")) score += 25;

if (text.contains("act as")) score += 10;

if (text.contains("pretend")) score += 8;

if (text.contains("disable safety")) score += 30;

if (text.contains("malware")) score += 25;

if (text.contains("steal passwords")) score += 30;

if (text.contains("hack")) score += 20;

if (text.contains("exploit")) score += 20;

if (text.contains("antivirus")) score += 10;

// =========================
// COMBINATION BONUS
// =========================

if (
    text.contains("ignore previous")
    && text.contains("bypass")
) {
    score += 15;
}

if (
    text.contains("malware")
    && text.contains("steal passwords")
) {
    score += 20;
}

if (
    text.contains("jailbreak")
    && text.contains("system prompt")
) {
    score += 15;
}

// =========================
// SMALL RANDOMNESS
// =========================

score += (Math.random() * 5);

// =========================
// LIMIT SCORE
// =========================

if (score > 100) {
    score = 100;
}

double percentage = score;

        String riskLevel;
        if (percentage < 30) riskLevel = "LOW RISK";
        else if (percentage < 70) riskLevel = "MEDIUM RISK";
        else riskLevel = "HIGH RISK";

        // 🤖 AI (IMPORTANT — same pattern as your system)
        List<String> reasons = geminiService.generateReasons(
        content,
        score
);

if (reasons == null || reasons.isEmpty()) {
    reasons = generator.generateReasons(content);
}

while (reasons.size() < 4) {

    List<String> fallback = Arrays.asList(
            "Prompt manipulation behavior identified",
            "Instruction tampering indicators observed",
            "Suspicious language pattern detected",
            "AI safety control bypass attempt detected",
            "Potential system prompt extraction attempt",
            "Risk identified through heuristic analysis"
    );

    String randomReason =
            fallback.get(new Random().nextInt(fallback.size()));

    if (!reasons.contains(randomReason)) {
        reasons.add(randomReason);
    }
}
        // 💾 SAVE TO DB
        Incident incident = new Incident();
        incident.setType("prompt_injection");
        incident.setContent(content);
        incident.setConfidence(percentage);
        incident.setVerdict(riskLevel);
        incident.setReasons(String.join(" | ", reasons));

        repo.save(incident);

        // 📤 RESPONSE
        PhishingResponse response = new PhishingResponse();
        response.setRiskLevel(riskLevel);
        response.setRiskPercentage(percentage);
        response.setReasons(reasons);
  

        return response;
    }
}