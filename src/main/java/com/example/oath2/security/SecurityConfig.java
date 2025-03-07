package com.example.oath2.security;

import com.example.oath2.user.UserRepository;
import com.example.oath2.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserRepository userRepository, JWTService jwtService, UserService userService, Oauth2SuccessHandler oauth2SuccessHandler) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).userDetailsService(userService).authorizeHttpRequests(auth ->
            auth
                    .requestMatchers("/api/create-user").permitAll()
                    .requestMatchers("/api/login-user").permitAll()
                    .anyRequest().authenticated())
                //.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oath2 -> {oath2.successHandler(oauth2SuccessHandler);
                        })
                .addFilterAfter(new Oauth2Filter(jwtService, userRepository), OAuth2LoginAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
