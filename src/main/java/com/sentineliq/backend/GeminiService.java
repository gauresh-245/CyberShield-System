package com.sentineliq.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public List<String> generateReasons(String input, double score) {

        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey;

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // ✅ BETTER PROMPT
            String prompt = "You are an advanced cybersecurity AI system.\n"
                  + "Analyze the following input and detect threats such as:\n"
                  + "- phishing\n"
                  + "- prompt injection\n"
                  + "- social engineering\n"
                  + "- malicious intent\n\n"
                  + "Input:\n" + input + "\n\n"
                  + "Return exactly 6 reasons.\n"
+ "Each reason must be on a new line.\n"
+ "Each reason must be less than 12 words.\n"
+ "Do not use numbering.\n"
+ "Do not use bullets.\n"
+ "Do not write explanations.\n"
                  + "Do not mention phishing unless relevant.\n"
                  + "Do not add headings or symbols.";
            

            JSONObject textPartObj = new JSONObject();

textPartObj.put("text", prompt);

JSONArray requestParts = new JSONArray();

requestParts.put(textPartObj);

JSONObject requestContent = new JSONObject();

requestContent.put("parts", requestParts);

JSONArray requestContents = new JSONArray();

requestContents.put(requestContent);

JSONObject requestBody = new JSONObject();

requestBody.put("contents", requestContents);

String jsonInput = requestBody.toString();


            OutputStream os = conn.getOutputStream();
            os.write(jsonInput.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("Gemini Response Code: " + responseCode);


            BufferedReader br = (responseCode == 200)
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            br.close();

            if (responseCode != 200) {
                System.out.println(response.toString());
                throw new RuntimeException("Gemini API failed");
            }

            JSONObject json = new JSONObject(response.toString());
            JSONArray candidates = json.getJSONArray("candidates");
            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");

            String text = parts.getJSONObject(0).getString("text");

            System.out.println("==============");
System.out.println("GEMINI OUTPUT");
System.out.println(text);
System.out.println("==============");

            String[] lines = text.split("\n");

            List<String> reasons = new ArrayList<>();

            for (String l : lines) {

                l = l.replaceAll("^\\d+\\.\\s*", "");
                l = l.replaceAll("\\*\\*", "");

                if (l.toLowerCase().contains("here are")) continue;

                l = l.trim();

                if (!l.isEmpty()) {
                    reasons.add(l);
                }
            }

            while (reasons.size() < 6) {

    List<String> fallback = Arrays.asList(
        "Threat indicators identified",
        "Suspicious behavior detected",
        "Potential security risk observed",
        "Abnormal instruction pattern found",
        "Input requires further verification",
        "AI heuristic alert triggered"
    );

    String randomReason =
        fallback.get(new Random().nextInt(fallback.size()));

    if(!reasons.contains(randomReason)) {
        reasons.add(randomReason);
    }
}
            return reasons.subList(0, 6);

        } catch (Exception e) {

    e.printStackTrace();

    return new ArrayList<>();
}
    }



    
public List<String> generateDeepfakeReasons(
        String input,
        double score) {

    try {

        String endpoint =
                "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key="
                        + apiKey;

        URL url = new URL(endpoint);

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty(
                "Content-Type",
                "application/json"
        );

        conn.setDoOutput(true);

        String prompt =
                "You are a Deepfake Detection AI.\n\n"

                + "Analyze the media details.\n\n"

                + "Generate ONLY deepfake-related reasons.\n\n"

                + "Possible indicators:\n"
                + "- AI generated media\n"
                + "- GAN artifacts\n"
                + "- Metadata anomalies\n"
                + "- Lighting mismatch\n"
                + "- Face inconsistencies\n"
                + "- Lip sync issues\n"
                + "- Synthetic content indicators\n\n"

                + "Do NOT mention:\n"
                + "- phishing\n"
                + "- prompt injection\n"
                + "- social engineering\n"
                + "- hacking\n\n"

                + "Input:\n"
                + input
                + "\n\n"

                + "Return exactly 6 reasons.\n"
                + "Each reason on a new line.\n"
                + "No numbering.\n"
                + "No bullets.";

        JSONObject textPartObj = new JSONObject();

        textPartObj.put("text", prompt);

        JSONArray requestParts =
                new JSONArray();

        requestParts.put(textPartObj);

        JSONObject requestContent =
                new JSONObject();

        requestContent.put(
                "parts",
                requestParts
        );

        JSONArray requestContents =
                new JSONArray();

        requestContents.put(
                requestContent
        );

        JSONObject requestBody =
                new JSONObject();

        requestBody.put(
                "contents",
                requestContents
        );

        String jsonInput =
                requestBody.toString();

        OutputStream os =
                conn.getOutputStream();

        os.write(jsonInput.getBytes());

        os.flush();

        os.close();

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()
                        )
                );

        StringBuilder response =
                new StringBuilder();

        String line;

        while ((line = br.readLine()) != null) {

            response.append(line);
        }

        br.close();

        JSONObject json =
                new JSONObject(
                        response.toString()
                );

        JSONArray candidates =
                json.getJSONArray(
                        "candidates"
                );

        JSONObject content =
                candidates
                        .getJSONObject(0)
                        .getJSONObject("content");

        JSONArray parts =
                content.getJSONArray(
                        "parts"
                );

        String text =
                parts.getJSONObject(0)
                        .getString("text");

        return Arrays.asList(
                text.split("\n")
        );

    } catch (Exception e) {

        e.printStackTrace();

        return new ArrayList<>();
    }
}
}

