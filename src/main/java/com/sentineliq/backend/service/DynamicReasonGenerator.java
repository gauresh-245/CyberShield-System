package com.sentineliq.backend.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

@Service
public class DynamicReasonGenerator {

    public List<String> generateReasons(String input) {

        List<String> reasons = new ArrayList<>();

        String text = input.toLowerCase();

        if(text.contains("ignore previous"))
            reasons.add("Instruction override attempt detected");

        if(text.contains("ignore all previous"))
            reasons.add("Multiple instruction override attempts identified");

        if(text.contains("system prompt"))
            reasons.add("Attempt to access hidden system prompts");

        if(text.contains("bypass"))
            reasons.add("Security bypass pattern detected");

        if(text.contains("override"))
            reasons.add("Command override behavior identified");

        if(text.contains("jailbreak"))
            reasons.add("Prompt jailbreak technique detected");

        if(text.contains("hack"))
            reasons.add("Potential offensive security request detected");

        if(text.contains("malware"))
            reasons.add("Malware-related terminology identified");

        if(text.contains("steal passwords"))
            reasons.add("Credential theft indicators detected");

        if(reasons.isEmpty()) {
            reasons.add("No malicious patterns detected");
            reasons.add("Input appears legitimate");
        }

        return reasons;
    }


    public List<String> generatePhishingReasons(String input) {

    List<String> reasons = new ArrayList<>();

    String text = input.toLowerCase();

    if(text.contains("urgent"))
        reasons.add("Urgency tactics detected");

    if(text.contains("click"))
        reasons.add("Suspicious call-to-action identified");

    if(text.contains("verify"))
        reasons.add("Account verification request detected");

    if(text.contains("password"))
        reasons.add("Credential-related content identified");

    if(text.contains("otp"))
        reasons.add("One-time password request detected");

    if(text.contains("bank"))
        reasons.add("Financial institution references found");

    if(text.contains("login"))
        reasons.add("Login-related request detected");

    if(reasons.isEmpty()) {
        reasons.add("No phishing indicators detected");
        reasons.add("Content appears legitimate");
    }

    return reasons;
}



public List<String> generateUrlReasons(String url) {

    List<String> reasons = new ArrayList<>();

    String text = url.toLowerCase();

    if(text.startsWith("http://"))
        reasons.add("Unencrypted HTTP connection detected");

    if(text.contains(".ru"))
        reasons.add("High-risk domain extension identified");

    if(text.contains("login"))
        reasons.add("Login-related URL pattern detected");

    if(text.contains("verify"))
        reasons.add("Verification request found in URL");

    if(text.contains("reset-password"))
        reasons.add("Password reset pattern detected");

    if(text.contains("bank"))
        reasons.add("Financial service reference identified");

    if(reasons.isEmpty()) {
        reasons.add("No suspicious URL indicators detected");
        reasons.add("URL structure appears normal");
    }

    return reasons;
}




public List<String> generateFileReasons(String content) {

    List<String> reasons = new ArrayList<>();

    String text = content.toLowerCase();

    if(text.contains("password"))
        reasons.add("Sensitive credential content detected");

    if(text.contains("otp"))
        reasons.add("One-time password references found");

    if(text.contains("bank"))
        reasons.add("Financial information detected");

    if(text.contains("urgent"))
        reasons.add("Urgency-based language identified");

    if(text.contains("click"))
        reasons.add("Potential phishing instruction detected");

    if(reasons.isEmpty()) {
        reasons.add("No suspicious file content detected");
        reasons.add("File appears legitimate");
    }

    return reasons;
}


}