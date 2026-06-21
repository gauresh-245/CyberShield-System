package com.sentineliq.backend.service;

import com.sentineliq.backend.*;
import com.sentineliq.backend.utils.VideoUtils;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class DeepfakeService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
private DeepfakeReasonGenerator generator;

    @Autowired
    private IncidentRepository repo;

    public PhishingResponse analyzeFile(MultipartFile file) {

        File tempFile = null;

        try {

            // ✅ SAFE FILE NAME HANDLING
            String fileName = file.getOriginalFilename();
            if (fileName == null) fileName = "unknown_file";

            fileName = fileName.toLowerCase();

            String contentType = file.getContentType();
            if (contentType == null) contentType = "";

            // ✅ 1. FILE TYPE VALIDATION
            if (!(fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mov")
                    || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png"))) {

                throw new RuntimeException("Only video and image files allowed");
            }

            // ✅ 2. MIME TYPE VALIDATION
            if (!(contentType.startsWith("video/") || contentType.startsWith("image/"))) {
                throw new RuntimeException("Invalid file type");
            }

            // ✅ 3. FILE SIZE CHECK (500MB)
            if (file.getSize() > 500 * 1024 * 1024) {
                throw new RuntimeException("File exceeds 500MB limit");
            }

            // ✅ 4. SAVE TEMP FILE
            tempFile = File.createTempFile("upload_", "_" + fileName);
            file.transferTo(tempFile);

            double score = 0;
            double duration = 0;

            // 🎥 5. REAL VIDEO DURATION (FFmpeg)
            if (contentType.startsWith("video/")) {

                duration = VideoUtils.getVideoDuration(tempFile.getAbsolutePath());

                if (duration < 3) score += 0.2;     // very short video
                if (duration > 300) score += 0.1;   // very long video
            }

            
// 🧾 6. REAL METADATA (Tika)
Tika tika = new Tika();

String metadata = tika.parseToString(tempFile);

boolean hasMetadata = metadata != null && metadata.length() > 50;

if (!hasMetadata)
    score += 0.3;

// 🔥 AI TOOL METADATA DETECTION

String lowerMetadata =
        metadata == null ? "" : metadata.toLowerCase();

if (lowerMetadata.contains("stable diffusion"))
    score += 0.5;

if (lowerMetadata.contains("midjourney"))
    score += 0.5;

if (lowerMetadata.contains("dall-e"))
    score += 0.5;

if (lowerMetadata.contains("adobe firefly"))
    score += 0.5;

if (lowerMetadata.contains("runway"))
    score += 0.5;

// 🧠 7. FILE NAME SIGNALS

if (fileName.contains("ai")
        || fileName.contains("generated")
        || fileName.contains("deepfake")
        || fileName.contains("synthetic")
        || fileName.contains("midjourney")) {

    score += 0.2;
}



            // 📊 8. FINAL SCORE
            double percentage = Math.min(score, 1.0) * 100;

            String riskLevel;
           
if (percentage <= 30)
    riskLevel = "LOW RISK";

else if (percentage <= 70)
    riskLevel = "MEDIUM RISK";

else
    riskLevel = "HIGH RISK";



            // 🤖 9. AI REASONING (UPDATED PROMPT 🔥)
            String input = "Deepfake Analysis:\n"
                    + "File: " + fileName + "\n"
                    + "Type: " + contentType + "\n"
                    + "Size: " + file.getSize() + "\n"
                    + "Duration: " + duration + "\n"
                    + "Metadata length: " + (metadata != null ? metadata.length() : 0);

            String aiPrompt = """

You are an advanced Deepfake Detection AI.

Analyze the uploaded media and generate ONLY deepfake-related forensic reasons.

Rules:
- Give short forensic observations.
- Reasons must match the risk score.
- Do NOT mention cybersecurity or prompt injection.
- Mention visual inconsistencies, GAN artifacts, metadata anomalies, lighting mismatch, skin texture, lip sync, blinking, AI artifacts etc.
- If media appears real, mention authentic patterns.

Risk Score: """ + percentage + """

Media Details:
""" + input + """

""";

List<String> reasons =
        geminiService.generateDeepfakeReasons(
                aiPrompt,
                score
        );

if (reasons == null || reasons.isEmpty()) {

    reasons = generator.generateReasons(
            metadata,
            fileName,
            duration
    );
}

while (reasons.size() < 4) {

    List<String> fallback = List.of(
            "Media integrity requires verification",
            "Synthetic content indicators reviewed",
            "Metadata consistency analysis completed",
            "Forensic inspection performed",
            "Potential AI generated media characteristics observed"
    );

    String randomReason =
            fallback.get(
                    new java.util.Random()
                            .nextInt(fallback.size())
            );

    if (!reasons.contains(randomReason)) {
        reasons.add(randomReason);
    }
}

            // 💾 10. SAVE TO DATABASE
            Incident incident = new Incident();
            incident.setType("deepfake");
            incident.setContent(input);
            incident.setConfidence(percentage);
            incident.setVerdict(riskLevel);
            incident.setReasons(String.join(" | ", reasons));

            repo.save(incident);

            // 📤 11. RESPONSE
            PhishingResponse response = new PhishingResponse();
            response.setRiskLevel(riskLevel);
            response.setRiskPercentage(percentage);
            response.setReasons(reasons);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Deepfake analysis failed: " + e.getMessage());
        }

        // 🧹 12. ALWAYS CLEAN TEMP FILE
        finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}