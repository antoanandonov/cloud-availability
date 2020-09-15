package com.andonov.cloud.availability.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class CallbackQuota {

    @Id
    @Column
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @OneToOne(mappedBy = "quota")
    private InternalCallback callback;

    private Integer instances;
    private String memory;
    private String disk;

    public CallbackQuota(Quota quota) {
        this.instances = quota.getInstances();
        this.memory = quota.getMemory();
        this.disk = quota.getDisk();
    }

}
