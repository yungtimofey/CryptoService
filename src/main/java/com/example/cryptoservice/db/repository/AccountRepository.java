package com.example.cryptoservice.db.repository;

import com.example.cryptoservice.db.models.AccountEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<AccountEntity, String> {
    Optional<AccountEntity> findAccountByName(String name);
}
