package com.tyne.finance.core.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigInteger;

@ToString
@Getter
@NoArgsConstructor
@Table(name = "core_accounttype")
@Entity
public class AccountType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger accountTypeID;

    private String code;

    private String name;
}
