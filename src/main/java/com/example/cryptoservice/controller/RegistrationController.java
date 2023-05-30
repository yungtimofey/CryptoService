package com.example.cryptoservice.controller;

import com.example.cryptoservice.models.RegistrationParams;
import com.example.cryptoservice.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public String login(@RequestBody RegistrationParams registrationParams) {
        return registrationService.login(registrationParams);
    }
}
