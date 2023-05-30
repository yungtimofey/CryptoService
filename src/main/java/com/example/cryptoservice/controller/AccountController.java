package com.example.cryptoservice.controller;

import com.example.cryptoservice.models.FileDTO;
import com.example.cryptoservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/info")
    public String getName(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping("/files-uploaded")
    public Set<FileDTO> getUploadedFiles(Authentication authentication) {
        return accountService.getUploadedFiles(authentication.getName());
    }

    @GetMapping("/files-downloaded")
    public Set<FileDTO> getDownloadedFiles(Authentication authentication) {
        return accountService.getDownloadedFiles(authentication.getName());
    }
}
