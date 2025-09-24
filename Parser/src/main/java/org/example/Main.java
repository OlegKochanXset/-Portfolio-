package org.example;

import java.io.IOException;
import java.util.Comparator; // Добавьте этот импорт
import java.util.Map;

public class Main {
    private static final String LOG_PATH = "C:\\Users\\Oleg\\Desktop\\ПРОЕКТ\\production_log.csv";

    public static void main(String[] args) {
        LogParser parser = new LogParser();
        try {
            System.out.println("Анализируем файл: " + LOG_PATH);
            parser.parseLogFile(LOG_PATH);
            printStatistics(parser);
        } catch (IOException e) {
            System.err.println("Ошибка при обработке файла:");
            System.err.println(e.getMessage());

            if (!new java.io.File(LOG_PATH).exists()) {
                System.err.println("Файл не существует по указанному пути!");
            } else if (!new java.io.File(LOG_PATH).canRead()) {
                System.err.println("Нет прав на чтение файла!");
            }
        }
    }

    private static void printStatistics(LogParser parser) {
        System.out.println("\n=== ОБЩАЯ СТАТИСТИКА ===");
        System.out.printf("%-20s | %10s\n", "Endpoint", "Запросов");
        System.out.println("--------------------- | ----------");

        parser.getTotalCounts().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // Исправлено
                .forEach(e -> System.out.printf("%-20s | %10d\n", e.getKey(), e.getValue()));

        Map.Entry<String, Integer> peakHour = parser.findPeakHour();
        if (peakHour != null) {
            System.out.printf("\n=== ПИКОВАЯ НАГРУЗКА (%s) ===\n", peakHour.getKey());
            System.out.printf("%-20s | %10s | %8s\n", "Endpoint", "Запросов", "Доля");
            System.out.println("--------------------- | ---------- | --------");

            int total = peakHour.getValue();
            parser.getHourlyStats().get(peakHour.getKey()).entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // Исправлено
                    .forEach(e -> {
                        double percentage = 100.0 * e.getValue() / total;
                        System.out.printf("%-20s | %10d | %7.2f%%\n",
                                e.getKey(), e.getValue(), percentage);
                    });
        }
    }
}