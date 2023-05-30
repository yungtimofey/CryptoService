package com.example.cryptoservice.service.impl;

import com.example.cryptoservice.db.models.AccountEntity;
import com.example.cryptoservice.db.models.AccountFileEntity;
import com.example.cryptoservice.db.models.FileStatusEntity;
import com.example.cryptoservice.db.repository.AccountFileRepository;
import com.example.cryptoservice.db.repository.AccountRepository;
import com.example.cryptoservice.db.repository.FileStatusRepository;
import com.example.cryptoservice.models.AccountStatusOnFile;
import com.example.cryptoservice.models.FileDTO;
import com.example.cryptoservice.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountFileRepository accountFileRepository;
    private final AccountRepository accountRepository;
    private final FileStatusRepository fileStatusRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public Set<FileDTO> getUploadedFiles(String username) {
        Optional<AccountEntity> accountEntityOptional = accountRepository.findAccountByName(username);
        AccountEntity accountEntity = accountEntityOptional.orElseThrow(() -> new IllegalArgumentException("Wrong user id"));

        String fileStatusInfo = AccountStatusOnFile.PRODUCER.name();
        FileStatusEntity fileStatusEntity = fileStatusRepository.getByInfo(fileStatusInfo);

        Set<AccountFileEntity> accountFileSet =
                accountFileRepository.getAccountFileEntitiesByAccountAndStatusEntity(accountEntity, fileStatusEntity);

        return accountFileSet.stream()
                .map(AccountFileEntity::getFile)
                .map(fileEntity -> mapper.map(fileEntity, FileDTO.class))
                .collect(Collectors.toSet());
    }


    @Override
    @Transactional
    public Set<FileDTO> getDownloadedFiles(String username) {
        Optional<AccountEntity> accountEntityOptional = accountRepository.findAccountByName(username);
        AccountEntity accountEntity = accountEntityOptional.orElseThrow(() -> new IllegalArgumentException("Wrong user id"));

        String fileStatusInfo = AccountStatusOnFile.CONSUMER.name();
        FileStatusEntity fileStatusEntity = fileStatusRepository.getByInfo(fileStatusInfo);

        Set<AccountFileEntity> accountFileSet =
                accountFileRepository.getAccountFileEntitiesByAccountAndStatusEntity(accountEntity, fileStatusEntity);

        return accountFileSet.stream()
                .map(AccountFileEntity::getFile)
                .map(fileEntity -> mapper.map(fileEntity, FileDTO.class))
                .collect(Collectors.toSet());
    }
}
