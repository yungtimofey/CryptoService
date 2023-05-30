package com.example.cryptoservice.db.repository;

import com.example.cryptoservice.db.models.FileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FileRepository extends CrudRepository<FileEntity, String> {
    Set<FileEntity> findAll();
    Set<FileEntity> findByNameContainingIgnoreCaseOrderByName(String name);
}
