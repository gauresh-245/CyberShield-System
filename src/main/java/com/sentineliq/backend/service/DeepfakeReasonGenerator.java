package com.sentineliq.backend.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeepfakeReasonGenerator {

    public List<String> generateReasons(
            String metadata,
            String fileName,
            double duration
    ) {

        List<String> reasons = new ArrayList<>();

        String text = metadata == null
                ? ""
                : metadata.toLowerCase();

        fileName = fileName.toLowerCase();

        if(text.contains("stable diffusion"))
            reasons.add("AI image generation metadata detected");

        if(text.contains("midjourney"))
            reasons.add("Synthetic media creation signature found");

        if(text.contains("dall-e"))
            reasons.add("AI generated content indicator detected");

        if(text.contains("runway"))
            reasons.add("AI video generation metadata identified");

        if(fileName.contains("deepfake"))
            reasons.add("Suspicious filename pattern detected");

        if(fileName.contains("generated"))
            reasons.add("Generated media naming convention found");

        if(duration < 3)
            reasons.add("Unusually short media duration observed");

        if(duration > 300)
            reasons.add("Extended media duration requires verification");

        if(reasons.isEmpty()) {

            reasons.add("No strong synthetic media indicators found");

            reasons.add("Metadata appears consistent");
        }

        return reasons;
    }
}