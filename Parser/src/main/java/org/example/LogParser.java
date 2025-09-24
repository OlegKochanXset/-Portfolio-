package org.example;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;

public class LogParser {
    private static final List<String> TARGET_ENDPOINTS = List.of(
            "/api/signDoc",
            "/api/sendMessage",
            "/api/getMessage",
            "/api/addDoc",
            "/api/getDocByName"
    );

    private final Map<String, Integer> totalCounts = new HashMap<>();
    private final Map<String, Map<String, Integer>> hourlyStats = new HashMap<>();

    public void parseLogFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLogLine(line);
            }
        }
    }

    private void processLogLine(String line) {
        String[] parts = line.split("~", 2);
        if (parts.length < 2) return;

        String timestamp = parts[0].trim();
        String request = parts[1].trim();

        if (request.contains("Mapped to")) return;

        String[] requestParts = request.split("\\s+", 2);
        if (requestParts.length < 2) return;

        String endpoint = normalizeEndpoint(requestParts[1].replace("\"", ""));
        if (!isTargetEndpoint(endpoint)) return;

        String hour = parseHour(timestamp);
        if (hour == null) return;

        totalCounts.merge(endpoint, 1, Integer::sum);
        hourlyStats.computeIfAbsent(hour, k -> new HashMap<>())
                .merge(endpoint, 1, Integer::sum);
    }

    private String normalizeEndpoint(String endpoint) {
        if (endpoint.startsWith("/api/sendMessage")) {
            return "/api/sendMessage";
        }
        return endpoint.split("\\?")[0];
    }

    private boolean isTargetEndpoint(String endpoint) {
        return TARGET_ENDPOINTS.stream().anyMatch(endpoint::startsWith);
    }

    private String parseHour(String timestamp) {
        try {
            Instant instant = Instant.parse(timestamp);
            return instant.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));
        } catch (Exception e) {
            System.err.println("Ошибка парсинга времени: " + timestamp);
            return null;
        }
    }

    public Map<String, Integer> getTotalCounts() {
        return Collections.unmodifiableMap(totalCounts);
    }

    public Map<String, Map<String, Integer>> getHourlyStats() {
        return Collections.unmodifiableMap(hourlyStats);
    }

    public Map.Entry<String, Integer> findPeakHour() {
        return hourlyStats.entrySet().stream()
                .map(e -> Map.entry(
                        e.getKey(),
                        e.getValue().values().stream().mapToInt(Integer::intValue).sum()
                ))
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }
}