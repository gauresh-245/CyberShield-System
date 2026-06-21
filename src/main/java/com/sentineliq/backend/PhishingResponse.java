package com.sentineliq.backend;

import java.util.List;

public class PhishingResponse {

    private String riskLevel;
    private double riskPercentage;
    private List<String> reasons;

    public String getRiskLevel() { return riskLevel; }
    public double getRiskPercentage() { return riskPercentage; }
    public List<String> getReasons() { return reasons; }

    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public void setRiskPercentage(double riskPercentage) { this.riskPercentage = riskPercentage; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
}