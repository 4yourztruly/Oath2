package com.example.oath2.security;

import com.example.oath2.user.User;
import com.example.oath2.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class Oauth2Filter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication potentialOauth2Authentication = SecurityContextHolder.getContext().getAuthentication();

        if(potentialOauth2Authentication != null) {

            if(potentialOauth2Authentication instanceof OAuth2AuthenticationToken oauth2Token) {
                OAuth2User oAuth2User = oauth2Token.getPrincipal();

                String oidcId = oAuth2User.getName();

                Optional<User> potentialUser = userRepository.findByOidcId(oidcId);
                if(potentialUser.isEmpty()) {
                    response.sendError(401,"Invalid token.");
                    return;
                }

                User user = potentialUser.get();

                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(),user.getAuthorities()));

                filterChain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request,response);
            return;
        }

        String jwtToken = authHeader.substring("Bearer ".length());
        if (jwtToken.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID userId;
        try{
            userId = jwtService.verifyToken(jwtToken);
        } catch (Exception e) {
            response.sendError(401, "Invalid token.");
            return;
        }

        Optional<User> potentialUser = userRepository.findById(userId);
        if(potentialUser.isEmpty()) {
            response.sendError(401, "Invalid token.");
            return;
        }

        User user = potentialUser.get();

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
        filterChain.doFilter(request,response);
    }
}
