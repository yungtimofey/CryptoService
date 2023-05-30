package com.example.cryptoservice.db.repository;

import com.example.cryptoservice.db.models.AccountEntity;
import com.example.cryptoservice.db.models.AccountFileEntity;
import com.example.cryptoservice.db.models.AccountFileKey;
import com.example.cryptoservice.db.models.FileStatusEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AccountFileRepository extends CrudRepository<AccountFileEntity, AccountFileKey> {
    Set<AccountFileEntity> getAccountFileEntitiesByAccountAndStatusEntity(
            AccountEntity account,
            FileStatusEntity statusEntity
    );

    boolean existsByKey(AccountFileKey accountFileKey);
}
