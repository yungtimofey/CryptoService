package com.example.cryptoservice.db.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "accounts_files")
public class AccountFileEntity {
    @EmbeddedId
    private AccountFileKey key;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @ManyToOne
    @MapsId("fileId")
    @JoinColumn(name = "file_id")
    private FileEntity file;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private FileStatusEntity statusEntity;
}
