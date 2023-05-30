package com.example.cryptoservice.controller;

import com.company.crypto.aesImpl.mode.SymmetricalBlockMode;
import com.company.crypto.benaloh.algorithm.Benaloh;
import com.example.cryptoservice.models.FileDTO;
import com.example.cryptoservice.models.FileToSave;
import com.example.cryptoservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Set;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addFile(@RequestParam("file") MultipartFile multipartFile,
                          @RequestParam("name") String fileName,
                          @RequestParam("key")  String encodedSymmetricalKey,
                          @RequestParam("mode") String mode,
                          @RequestParam("iv") String iv,
                          @RequestParam("index-for-ctr") int index,
                          @RequestParam("hash") String hash,
                          Authentication authentication) {

        FileToSave file = new FileToSave();
        file.setFile(multipartFile);
        file.setFileName(fileName);
        file.setEncodedSymmetricalKey(encodedSymmetricalKey);
        file.setMode(SymmetricalBlockMode.valueOf(mode));
        file.setIv(iv);
        file.setIndexForCTR(index);
        file.setHash(hash);

        return fileService.addFile(authentication.getName(), file);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable("id") String fileId, Authentication authentication) {
        return fileService.getFile(authentication.getName(), fileId);
    }

    @GetMapping(path = "/cipher-key")
    public String getFile(@RequestParam("id") String fileId,
                          @RequestParam("y") BigInteger y,
                          @RequestParam("r") BigInteger r,
                          @RequestParam("n") BigInteger n) {

        return fileService.getEncodedSymmetricalKeyOfFile(fileId, new Benaloh.OpenKey(y, r, n));
    }

    @GetMapping(path = "/all")
    public Set<FileDTO> getAllFiles() {
        return fileService.getAllFiles();
    }

    @GetMapping(path = "/open-key")
    public Benaloh.OpenKey getOpenKey() {
        return fileService.getOpenKey();
    }

    @GetMapping(path = "/search")
    public Set<FileDTO> getSearchFiles(@RequestParam("name") String name) {
        return fileService.getSearchFiles(name);
    }
}
