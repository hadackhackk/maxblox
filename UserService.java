package com.maxblox.service;

import com.maxblox.dto.RegisterRequest;
import com.maxblox.model.User;
import com.maxblox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    public String registerUser(RegisterRequest request) {
        // Проверка возраста
        int age = Period.between(request.getBirthDate(), LocalDate.now()).getYears();
        if (age < 13) {
            return "Для регистрации необходимо быть старше 13 лет";
        }
        
        // Проверка имени пользователя
        if (userRepository.existsByUsername(request.getUsername())) {
            return "Имя пользователя уже занято";
        }
        
        // Проверка email
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email уже зарегистрирован";
        }
        
        // Проверка паролей
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Пароли не совпадают";
        }
        
        // Создание пользователя
        User user = new User(
            request.getUsername(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getGender(),
            request.getAvatar(),
            request.getBirthDate()
        );
        
        // Генерация кода верификации
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);
        
        // Сохранение пользователя
        userRepository.save(user);
        
        // Отправка email для верификации
        emailService.sendVerificationEmail(user.getEmail(), verificationCode);
        
        return "success";
    }
    
    public boolean verifyUser(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);
        if (user != null && !user.isActive()) {
            user.setActive(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}