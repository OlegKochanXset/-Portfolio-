package com.example.stubservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;  // Импорт для Collections.singletonMap
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController  // Аннотация Spring для REST контроллера
@RequestMapping("/app/v1")  // Базовый путь для всех эндпоинтов
public class StubController {
    private static final Logger log = LoggerFactory.getLogger(StubController.class);  // Инициализация логгера

    @GetMapping("/getRequest")  // Обработка GET запроса
    public ResponseEntity<String> getRequest(
            @RequestParam int id,  // Параметр id из URL
            @RequestParam String name) throws Exception {  // Параметр name из URL

        // Валидация id
        if (id <= 10) {
            log.error("ID must be > 10");  // Логирование ошибки
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ID must be > 10");  // Возврат ошибки 500
        }

        // Валидация name
        if (name == null || name.length() <= 5) {
            log.error("Name length must be > 5");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Name length must be > 5");
        }

        // Имитация задержки обработки
        TimeUnit.MILLISECONDS.sleep(id > 10 && id < 50 ? 1000 : 500);

        // Чтение файла шаблона
        try (InputStream inputStream = getClass().getResourceAsStream("/static/getAnswer.txt")) {
            if (inputStream == null) {
                log.error("File not found: getAnswer.txt");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Configuration error");
            }

            // Подстановка параметров в шаблон
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("{id}", String.valueOf(id))
                    .replace("{name}", name);
            return ResponseEntity.ok(content);  // Возврат успешного ответа
        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }

    @PostMapping("/postRequest")
    public ResponseEntity<Map<String, Object>> postRequest(@RequestBody Map<String, Object> requestBody) {
        // Проверка на null всего тела запроса
        if (requestBody == null) {
            log.error("Request body is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Request body cannot be null"));
        }

        // Проверка наличия всех обязательных полей
        if (!requestBody.containsKey("name") ||
                !requestBody.containsKey("surname") ||
                !requestBody.containsKey("age")) {
            log.error("Missing required fields");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "All fields (name, surname, age) are required"));
        }

        // Получаем значения полей
        Object nameObj = requestBody.get("name");
        Object surnameObj = requestBody.get("surname");
        Object ageObj = requestBody.get("age");

        // Проверка на null каждого поля
        if (nameObj == null || surnameObj == null || ageObj == null) {
            log.error("Field values cannot be null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Field values cannot be null"));
        }

        // Преобразование в строки и проверка на пустоту
        String name = nameObj.toString();
        String surname = surnameObj.toString();
        String ageStr = ageObj.toString();

        if (name.trim().isEmpty() || surname.trim().isEmpty() || ageStr.trim().isEmpty()) {
            log.error("Fields cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Fields cannot be empty"));
        }

        // Проверка что age является числом
        try {
            int age = Integer.parseInt(ageStr);
            int doubledAge = age * 2;

            // Формирование ответа
            Map<String, Object> response = new LinkedHashMap<>();

            Map<String, Object> person1 = new LinkedHashMap<>();
            person1.put("name", name);
            person1.put("surname", surname);
            person1.put("age", age);

            Map<String, Object> person2 = new LinkedHashMap<>();
            person2.put("name", surname); // Меняем местами
            person2.put("surname", name);
            person2.put("age", doubledAge);

            response.put("Person1", person1);
            response.put("Person2", person2);

            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            log.error("Invalid age format: {}", ageStr);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Age must be a valid number"));
        }
    }
}

// post через Power shell: $response = Invoke-RestMethod -Uri "http://localhost:8080/app/v1/postRequest" -Method Post -Body '{"name":"Ivan","surname":"Petrov","age":30}' -ContentType "application/json"
//$response | ConvertTo-Json -Depth 10
// get: curl "http://localhost:8080/app/v1/getRequest?id=15&name=TestUser"
//      curl "http://localhost:8080/app/v1/getRequest?id=110&name=Igorrr"