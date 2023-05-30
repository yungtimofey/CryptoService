package com.example.cryptoservice.service;

import com.example.cryptoservice.models.FileDTO;

import java.util.Set;

public interface AccountService {
    Set<FileDTO> getUploadedFiles(String username);
    Set<FileDTO> getDownloadedFiles(String username);
}
