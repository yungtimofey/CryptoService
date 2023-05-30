package com.example.cryptoservice.db.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "account")
public class AccountEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id", nullable = false)
    private String id;

    @NotNull(message = "Name should be not null!")
    @Column(unique=true)
    private String name;

    @NotNull(message = "Password should be not null!")
    private String password;

    @OneToMany(mappedBy = "account")
    private Set<AccountFileEntity> accountFiles;
}
