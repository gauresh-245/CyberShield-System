package com.sentineliq.backend.dto;

public class UserActivityRequest {

    private String userId;
    private String location;
    private String device;
    private String ip;
    private int loginAttempts;
    private String time; // "23:30"

    // Getters & Setters
    public String getUserId() { return userId; }
    public String getLocation() { return location; }
    public String getDevice() { return device; }
    public String getIp() { return ip; }
    public int getLoginAttempts() { return loginAttempts; }
    public String getTime() { return time; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setLocation(String location) { this.location = location; }
    public void setDevice(String device) { this.device = device; }
    public void setIp(String ip) { this.ip = ip; }
    public void setLoginAttempts(int loginAttempts) { this.loginAttempts = loginAttempts; }
    public void setTime(String time) { this.time = time; }
}