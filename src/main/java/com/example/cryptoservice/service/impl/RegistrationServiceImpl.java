package com.example.cryptoservice.service.impl;

import com.example.cryptoservice.models.RegistrationParams;
import com.example.cryptoservice.service.JwtTokenService;
import com.example.cryptoservice.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    @Override
    public String login(RegistrationParams registrationParams) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                registrationParams.getUsername(),
                registrationParams.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(token); // если пользователя нет, то выбросит ошибку!

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> authoritiesString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return jwtTokenService.createToken(authentication.getName(), registrationParams.getUsername(), authoritiesString);
    }
}
