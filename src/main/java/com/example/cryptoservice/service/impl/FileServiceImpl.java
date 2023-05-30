package com.example.cryptoservice.service.impl;

import com.company.crypto.benaloh.algorithm.Benaloh;
import com.example.cryptoservice.db.models.*;
import com.example.cryptoservice.db.repository.AccountFileRepository;
import com.example.cryptoservice.db.repository.AccountRepository;
import com.example.cryptoservice.db.repository.FileRepository;
import com.example.cryptoservice.db.repository.FileStatusRepository;
import com.example.cryptoservice.models.AccountStatusOnFile;
import com.example.cryptoservice.models.FileDTO;
import com.example.cryptoservice.models.FileToSave;
import com.example.cryptoservice.service.FileService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountFileRepository accountFileRepository;

    @Autowired
    private FileStatusRepository fileStatusRepository;

    @Autowired
    private Benaloh benaloh;

    @Autowired
    private ModelMapper mapper;

    private final Map<String, File> fileNamesAndFiles = new HashMap<>();
    private final String storageFileDirName;

    private final byte[] buffer = new byte[1024];

    public FileServiceImpl() {
        URL resource = getClass().getClassLoader().getResource("files");
        if (resource == null) {
            throw new IllegalStateException("Can't find file storage!");
        }

        storageFileDirName = resource.getFile();

        File dir = new File(storageFileDirName);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        Arrays.stream(files).forEach(file -> fileNamesAndFiles.put(file.getName(), file));
    }

    @Override
    public String addFile(String username, FileToSave fileToSave) {
        if (fileNamesAndFiles.containsKey(fileToSave.getFileName())) {
            throw new IllegalArgumentException(String.format("File with name %s already exists in storage!", fileToSave.getFileName()));
        }

        Optional<AccountEntity> accountEntityOptional = accountRepository.findAccountByName(username);
        AccountEntity accountEntity = accountEntityOptional.orElseThrow(() -> new IllegalArgumentException("Can't find account:" + username));

        File file;
        try {
            file = tryToTranslateMultipartFileToFile(fileToSave.getFile(), storageFileDirName + "/" + fileToSave.getFileName());
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't save file in storage!");
        }

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(file.getName());
        fileEntity.setBytes(file.length());
        fileEntity.setEncodedSymmetricalKey(fileToSave.getEncodedSymmetricalKey());
        fileEntity.setMode(fileToSave.getMode());
        fileEntity.setIv(fileToSave.getIv());
        fileEntity.setIndexForStr(fileToSave.getIndexForCTR());
        fileEntity.setHash(fileToSave.getHash());
        fileRepository.save(fileEntity);

        saveUserActionWithFileInDB(accountEntity, fileEntity, AccountStatusOnFile.PRODUCER);

        fileNamesAndFiles.put(fileToSave.getFileName(), file);
        return fileEntity.getId();
    }

    private File tryToTranslateMultipartFileToFile(MultipartFile multipartFile, String filePath) throws IOException {
        try (
                BufferedInputStream inputStream = new BufferedInputStream(multipartFile.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
        ) {
            int read;
            while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }
        return new File(filePath);
    }

    private void saveUserActionWithFileInDB(AccountEntity accountEntity, FileEntity fileEntity, AccountStatusOnFile fileStatus) {
        FileStatusEntity fileStatusEntity = fileStatusRepository.getByInfo(fileStatus.name());

        AccountFileKey accountFileKey = new AccountFileKey();
        accountFileKey.setAccountId(accountEntity.getId());
        accountFileKey.setFileId(fileEntity.getId());
        if (accountFileRepository.existsByKey(accountFileKey)) {
            return;
        }

        AccountFileEntity accountFileEntity = new AccountFileEntity();
        accountFileEntity.setKey(accountFileKey);
        accountFileEntity.setFile(fileEntity);
        accountFileEntity.setStatusEntity(fileStatusEntity);
        accountFileEntity.setAccount(accountEntity);
        accountFileRepository.save(accountFileEntity);
    }

    @Override
    public ResponseEntity<Resource> getFile(String username, String fileId) {
        Optional<FileEntity> fileEntityOptional = fileRepository.findById(fileId);
        FileEntity fileEntity = fileEntityOptional.orElseThrow(() -> new IllegalArgumentException("No file with id:" + fileId));

        Optional<AccountEntity> accountEntityOptional = accountRepository.findAccountByName(username);
        AccountEntity accountEntity = accountEntityOptional.orElseThrow(() -> new IllegalArgumentException("Can't find account with id:" + username));

        String fileName = fileEntity.getName();
        if (!fileNamesAndFiles.containsKey(fileName)) {
            throw new IllegalArgumentException(String.format("No file with name <<%s>> in storage!", fileName));
        }

        ResponseEntity<Resource> fileTranslatedIntoResource = tryToTranslateFileIntoResource(fileNamesAndFiles.get(fileName));
        saveUserActionWithFileInDB(accountEntity, fileEntity, AccountStatusOnFile.CONSUMER);

        return fileTranslatedIntoResource;
    }

    private ResponseEntity<Resource> tryToTranslateFileIntoResource(File file) {
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Can't translate file %s to resource", file.getName()));
        }
    }

    @Override
    public String getEncodedSymmetricalKeyOfFile(String fileId, Benaloh.OpenKey userKey) {
        Optional<FileEntity> fileEntityOptional = fileRepository.findById(fileId);
        FileEntity fileEntity = fileEntityOptional.orElseThrow(() -> new IllegalArgumentException(
                "No file with id:" + fileId));

        String fileName = fileEntity.getName();
        if (!fileNamesAndFiles.containsKey(fileName)) {
            throw new IllegalArgumentException(String.format(
                    "No file with name <<%s>> in storage!", fileName));
        }

        byte[] oneDSymmetricalKey = Base64.getDecoder().decode(fileEntity.getEncodedSymmetricalKey());
        byte[][] twoDSymmetricalKey = translateOneDSymmetricalKeyToTwoD(oneDSymmetricalKey);
        byte[] symmetricalCipherKey = decodeTwoDSymmetricalKey(twoDSymmetricalKey);

        byte[][] encodedSymmetricalKey = new byte[symmetricalCipherKey.length][];
        int numberOfBytesInEncodedCipherKey = 0;
        for (int i = 0; i < symmetricalCipherKey.length; i++) {
            byte byteOfCipherKey = symmetricalCipherKey[i];
            byte[] encodedByteOfCipherKey = benaloh.encode(new byte[]{byteOfCipherKey}, userKey);

            assert encodedByteOfCipherKey.length < Byte.MAX_VALUE;

            numberOfBytesInEncodedCipherKey += encodedByteOfCipherKey.length;
            encodedSymmetricalKey[i] = encodedByteOfCipherKey;
        }
        return Base64.getEncoder().encodeToString(
                convertTwoDArrayToOneD(encodedSymmetricalKey, numberOfBytesInEncodedCipherKey)
        );
    }

    private byte[][] translateOneDSymmetricalKeyToTwoD(byte[] oneDSymmetricalKey) {
        int sizeOfKey = 0;
        int arrayPtr = 0;
        while (arrayPtr < oneDSymmetricalKey.length) {
            int size = oneDSymmetricalKey[arrayPtr++];
            arrayPtr += size;
            sizeOfKey++;
        }

        arrayPtr = 0;

        byte[][] twoDKey = new byte[sizeOfKey][];
        for (int i = 0; i < sizeOfKey; i++) {
            int size = oneDSymmetricalKey[arrayPtr++];
            twoDKey[i] = new byte[size];

            System.arraycopy(oneDSymmetricalKey, arrayPtr, twoDKey[i], 0, size);
            arrayPtr += size;
        }
        return twoDKey;
    }

    private byte[] decodeTwoDSymmetricalKey(byte[][] twoDSymmetricalKey) {
        byte[] decodedCipherKey = new byte[twoDSymmetricalKey.length];
        for (int i = 0; i < twoDSymmetricalKey.length; i++) {
            byte[] decodedByte = benaloh.decode(twoDSymmetricalKey[i]);
            assert decodedByte.length == 1;
            decodedCipherKey[i] = decodedByte[0];
        }
        return decodedCipherKey;
    }

    private byte[] convertTwoDArrayToOneD(byte[][] twoDArray, int numberOfBytes) {
        byte[] oneDArray = new byte[numberOfBytes + twoDArray.length];

        int ptr = 0;
        for (byte[] array : twoDArray) {
            oneDArray[ptr++] = (byte) array.length;
            System.arraycopy(array, 0, oneDArray, ptr, array.length);
            ptr += array.length;
        }
        return oneDArray;
    }

    @Override
    public Benaloh.OpenKey getOpenKey() {
        return benaloh.getOpenKey();
    }


    @Override
    public Set<FileDTO> getAllFiles() {
        return fileRepository.findAll()
                .stream()
                .map(fileEntity -> mapper.map(fileEntity, FileDTO.class))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public Set<FileDTO> getSearchFiles(String name) {
        return fileRepository.findByNameContainingIgnoreCaseOrderByName(name)
                .stream()
                .map(fileEntity -> mapper.map(fileEntity, FileDTO.class))
                .collect(Collectors.toSet());
    }
}
