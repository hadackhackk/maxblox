package com.maxblox.controller;

import com.maxblox.dto.RegisterRequest;
import com.maxblox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080") // URL вашего фронтенда
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = userService.registerUser(request);
            
            if ("success".equals(result)) {
                response.put("success", true);
                response.put("message", "Аккаунт успешно создан! Проверьте email для подтверждения.");
            } else {
                response.put("success", false);
                response.put("message", result);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ошибка сервера: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String code) {
        boolean verified = userService.verifyUser(code);
        if (verified) {
            return ResponseEntity.ok("Email успешно подтвержден! Теперь вы можете войти в систему.");
        } else {
            return ResponseEntity.badRequest().body("Неверный или устаревший код подтверждения.");
        }
    }
    
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@PathVariable String username) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", !userService.usernameExists(username));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@PathVariable String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", !userService.emailExists(email));
        return ResponseEntity.ok(response);
    }
}