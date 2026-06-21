package com.sentineliq.backend.utils;

import java.util.*;

public class UrlAnalyzer {

    public static List<String> analyze(String url) {

        List<String> reasons = new ArrayList<>();

        String lower = url.toLowerCase();

        // 1. HTTPS check
        if (!lower.startsWith("https")) {
            reasons.add("URL is not secure (no HTTPS)");
        }

        // 2. Suspicious keywords
        String[] keywords = {"login", "verify", "bank", "secure", "update", "account"};

        for (String k : keywords) {
            if (lower.contains(k)) {
                reasons.add("Contains suspicious keyword: " + k);
            }
        }

        // 3. URL length
        if (url.length() > 70) {
            reasons.add("URL is unusually long");
        }

        // 4. IP-based URL
        if (url.matches(".*\\d+\\.\\d+\\.\\d+\\.\\d+.*")) {
            reasons.add("Uses IP address instead of domain");
        }

        // 5. Too many dots
        if (url.split("\\.").length > 4) {
            reasons.add("Too many subdomains (possible spoofing)");
        }

        return reasons;
    }
}