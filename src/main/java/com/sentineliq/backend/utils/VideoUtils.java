package com.sentineliq.backend.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class VideoUtils {

    public static double getVideoDuration(String filePath) {
        try {

            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-i", filePath
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.contains("Duration")) {

                    String duration = line.split(",")[0].split("Duration:")[1].trim();

                    String[] parts = duration.split(":");

                    double hours = Double.parseDouble(parts[0]);
                    double minutes = Double.parseDouble(parts[1]);
                    double seconds = Double.parseDouble(parts[2]);

                    return hours * 3600 + minutes * 60 + seconds;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}