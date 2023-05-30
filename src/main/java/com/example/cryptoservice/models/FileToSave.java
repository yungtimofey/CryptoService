package com.example.cryptoservice.models;

import com.company.crypto.aesImpl.mode.SymmetricalBlockMode;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileToSave {
    private MultipartFile file;
    private String fileName;
    private String encodedSymmetricalKey;
    private SymmetricalBlockMode mode;
    private String iv;
    private int indexForCTR;
    private String hash;
    private String key;
}
