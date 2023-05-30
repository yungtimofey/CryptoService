package com.example.cryptoservice.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.cryptoservice.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private JwtTokenService jwtTokenService;
    private static final String TOKEN_HEADER = "token";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(TOKEN_HEADER);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String name = jwtTokenService.getUsername(token);
            List<? extends GrantedAuthority> authorities = jwtTokenService.getAuthorities(token)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    name,
                    null,
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            filterChain.doFilter(request, response);
        }
    }
}