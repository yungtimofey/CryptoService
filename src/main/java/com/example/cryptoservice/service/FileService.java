package com.example.cryptoservice.service;

import com.company.crypto.benaloh.algorithm.Benaloh;
import com.example.cryptoservice.models.FileDTO;
import com.example.cryptoservice.models.FileToSave;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface FileService {
    String addFile(String username, FileToSave file);

    ResponseEntity<Resource> getFile(String username, String fileId);

    String getEncodedSymmetricalKeyOfFile(String fileId, Benaloh.OpenKey userKey);

    Benaloh.OpenKey getOpenKey();

    Set<FileDTO> getAllFiles();

    Set<FileDTO> getSearchFiles(String name);
}
