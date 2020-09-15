package com.andonov.cloud.availability.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CallbackCredentials {

    @Id
    @Column
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @OneToOne(mappedBy = "credentials")
    private InternalCallback callback;

    private String appName;
    private String appUri;
    private String api;
    private String org;
    private String space;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private byte[] password;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String encryptionKey;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String encryptionVector;

    public CallbackCredentials(Credentials credentials) {
        this.appName = credentials.getAppName();
        this.appUri = credentials.getAppUri();
        this.api = credentials.getApi();
        this.org = credentials.getOrg();
        this.space = credentials.getSpace();
        this.user = credentials.getUser();
        this.password = credentials.getPassword();
        this.encryptionKey = credentials.getEncryptionKey();
        this.encryptionVector = credentials.getEncryptionVector();
    }

}
