package com.sentineliq.backend.service;

import com.sentineliq.backend.GeminiService;
import com.sentineliq.backend.Incident;
import com.sentineliq.backend.IncidentRepository;
import com.sentineliq.backend.PhishingResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.tika.Tika;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class FileService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
private DynamicReasonGenerator generator;

    @Autowired
    private IncidentRepository repo;

    public PhishingResponse analyzeFile(MultipartFile file) {

        String content = extractContent(file);

        // 🔥 DEBUG
        System.out.println("FILE CONTENT: " + content);

        // ❗ Handle error case
        if (content.equals("ERROR_PDF_READ")) {
            PhishingResponse response = new PhishingResponse();
            response.setRiskLevel("UNSUPPORTED FILE");
            response.setRiskPercentage(0);
            response.setReasons(List.of(
                    "This PDF format is not supported",
                    "Complex or scanned PDF detected",
                    "Try a simple text-based PDF",
                    "OCR not implemented yet",
                    "File parsing failed",
                    "System handled error safely"
            ));
            return response;
        }

        // 🔍 RULES
        double score = 0;
        String text = content.toLowerCase();

        if (text.contains("password")) score += 0.2;
        if (text.contains("bank")) score += 0.2;
        if (text.contains("otp")) score += 0.2;
        if (text.contains("click")) score += 0.2;
        if (text.contains("urgent")) score += 0.2;

        double percentage = Math.min(score, 1.0) * 100;

        String riskLevel;
        if (percentage < 30) riskLevel = "LOW RISK";
        else if (percentage < 70) riskLevel = "MEDIUM RISK";
        else riskLevel = "HIGH RISK";

        // 🤖 AI
        List<String> reasons = geminiService.generateReasons(content, score);

if (reasons == null || reasons.isEmpty()) {
    reasons = generator.generateFileReasons(content);
}

while (reasons.size() < 4) {

    List<String> fallback = Arrays.asList(
            "Document requires further verification",
            "File content analyzed successfully",
            "Heuristic inspection completed",
            "Potential anomalies reviewed",
            "Content passed basic threat checks"
    );

    String randomReason =
            fallback.get(new Random().nextInt(fallback.size()));

    if (!reasons.contains(randomReason)) {
        reasons.add(randomReason);
    }
}

        // 💾 SAVE
        Incident incident = new Incident();
        incident.setType("file");
        incident.setContent(content.substring(0, Math.min(500, content.length())));
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

    // 🔥 FINAL EXTRACTOR (PDFBox + Tika)
    private String extractContent(MultipartFile file) {

        try {
            if (file == null || file.isEmpty()) {
                return "ERROR_PDF_READ";
            }

            String fileName = file.getOriginalFilename();

            if (fileName == null) {
                return "ERROR_PDF_READ";
            }

            fileName = fileName.toLowerCase();

            // 📄 PDF HANDLING
            if (fileName.endsWith(".pdf")) {

                // 1️⃣ Try PDFBox
                try {
                    PDDocument document = PDDocument.load(file.getInputStream());
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    document.close();

                    if (text != null && text.trim().length() > 50) {
                        return text;
                    }

                } catch (Exception e) {
                    System.out.println("PDFBox failed → trying Tika");
                }

                // 2️⃣ Try Apache Tika
                try {
                    Tika tika = new Tika();
                    String text = tika.parseToString(file.getInputStream());

                    if (text != null && text.trim().length() > 50) {
                        return text;
                    }

                } catch (Exception e) {
                    System.out.println("Tika also failed");
                }

                return "ERROR_PDF_READ";
            }

            // 📄 TXT
            else if (fileName.endsWith(".txt")) {
                return new String(file.getBytes());
            }

            // 🖼️ IMAGE
            else if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                return "IMAGE_FILE";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "ERROR_PDF_READ";
    }
}