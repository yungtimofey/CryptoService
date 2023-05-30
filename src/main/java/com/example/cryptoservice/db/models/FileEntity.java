package com.example.cryptoservice.db.models;

import com.company.crypto.aesImpl.mode.SymmetricalBlockMode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "file")
public class FileEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id", nullable = false)
    private String id;

    @NotNull(message = "File name required!")
    @Column(unique = true)
    private String name;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDate addDate;

    @Min(0)
    private Long bytes;

    private SymmetricalBlockMode mode;

    @Column(length = 1024)
    private String iv;

    private int indexForStr;

    @Column(length = 1024)
    private String hash;

    @Column(name = "key", length = 1024)
    private String encodedSymmetricalKey;

    @OneToMany(mappedBy = "file")
    private Set<AccountFileEntity> accountFiles;
}
