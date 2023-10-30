package com.tyne.finance.core.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigInteger;

@ToString
@Getter
@NoArgsConstructor
@Table(name = "core_currency")
@Entity
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger currencyID;

    private String symbol;

    private String code;

    private String country;
}
