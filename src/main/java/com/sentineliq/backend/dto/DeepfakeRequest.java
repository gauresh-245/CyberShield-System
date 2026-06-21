package com.sentineliq.backend.dto;

public class DeepfakeRequest {

    private String fileName;
    private String fileType; // audio/video/image
    private double duration;
    private String resolution;
    private boolean hasMetadata;
    private boolean faceDetected;

    // Getters & Setters
    public String getFileName() { return fileName; }
    public String getFileType() { return fileType; }
    public double getDuration() { return duration; }
    public String getResolution() { return resolution; }
    public boolean isHasMetadata() { return hasMetadata; }
    public boolean isFaceDetected() { return faceDetected; }

    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setDuration(double duration) { this.duration = duration; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public void setHasMetadata(boolean hasMetadata) { this.hasMetadata = hasMetadata; }
    public void setFaceDetected(boolean faceDetected) { this.faceDetected = faceDetected; }
}