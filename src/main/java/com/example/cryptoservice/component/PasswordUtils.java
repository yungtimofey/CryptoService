package com.example.cryptoservice.component;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {
    @Bean
    public PasswordEncoder mainEncoder(){
        return new BCryptPasswordEncoder();
    }
}
