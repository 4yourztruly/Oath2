package com.example.oath2.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderConfig {


    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
