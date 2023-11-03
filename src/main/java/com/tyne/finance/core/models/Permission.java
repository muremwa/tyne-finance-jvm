package com.tyne.finance.core.models;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;


@Data
@Table(name = "auth_permission")
@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger permissionID;

    private String codename;

    private String name;
}
