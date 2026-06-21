package com.sentineliq.backend;

import jakarta.persistence.*;

@Entity
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    // 🔥 FIX: allow large PDF content
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private String verdict;

    private double confidence;

    // 🔥 FIX: allow large AI reasons also
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reasons;

    // ✅ Getters
    public Long getId() { return id; }
    public String getType() { return type; }
    public String getContent() { return content; }
    public String getVerdict() { return verdict; }
    public double getConfidence() { return confidence; }
    public String getReasons() { return reasons; }

    // ✅ Setters
    public void setId(Long id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setContent(String content) { this.content = content; }
    public void setVerdict(String verdict) { this.verdict = verdict; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public void setReasons(String reasons) { this.reasons = reasons; }
}