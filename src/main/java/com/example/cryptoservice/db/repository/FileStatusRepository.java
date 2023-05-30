package com.example.cryptoservice.db.repository;

import com.example.cryptoservice.db.models.FileStatusEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileStatusRepository extends CrudRepository<FileStatusEntity, String> {
    FileStatusEntity getByInfo(String info);
}
