package com.example.oath2.security;

import com.example.oath2.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Deprecated
@RequiredArgsConstructor
public class VerifyJWTFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authenticationHeader = request.getHeader("Authorization");
        if (authenticationHeader == null || authenticationHeader.isBlank() || authenticationHeader.length() <= "Bearer ".length()) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authenticationHeader.substring("Bearer ".length());
        if (jwtToken.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        userService.verifyAuthentication(jwtToken).ifPresent(user -> {
            var authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    user.getPassword(),
                    user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        });

        filterChain.doFilter(request, response);
    }
}
