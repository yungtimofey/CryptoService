package com.example.cryptoservice.db.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class AccountFileKey implements Serializable {
    @Column(name = "account_id")
    private String accountId;

    @Column(name = "file_id")
    private String fileId;
}
