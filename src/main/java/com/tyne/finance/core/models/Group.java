package com.tyne.finance.core.models;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;

@Data
@Table(name = "auth_group")
@Entity
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger groupID;

    private String name;
}
