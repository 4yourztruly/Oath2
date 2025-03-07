package com.example.oath2.security;

import com.example.oath2.user.User;
import com.example.oath2.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JWTService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;

        OAuth2User oAuth2User = oAuth2Token.getPrincipal();

        System.out.println(oAuth2Token.getAuthorizedClientRegistrationId());

        String oidcId = oAuth2User.getName();

        String username = oAuth2User.getAttribute("login");

        Optional<User> existingUser = userService.findByOpenId(oidcId);

        if(existingUser.isEmpty()) {
            User user = userService.createOpenIdUser(username, oidcId);
            System.out.println(user.getUsername() + "created trough open id");
        }

        if(existingUser.isPresent()) {
            System.out.println(jwtService.generateToken(existingUser.get().getId()));
        }
    }
}
