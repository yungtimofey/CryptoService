package com.example.cryptoservice.models;

import com.company.crypto.aesImpl.mode.SymmetricalBlockMode;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FileDTO {
    private String id;
    private String name;
    private Long bytes;
    private LocalDate addDate;
    private String encodedSymmetricalKey;
    private String iv;
    private String hash;
    private int indexForCTR;
    private SymmetricalBlockMode mode;
}
