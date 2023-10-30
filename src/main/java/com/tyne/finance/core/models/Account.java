package com.tyne.finance.core.models;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;


@Data
@Table(name = "core_account")
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger accountID;

    @Column(name = "account_provider")
    private String accountProvider;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "date_added")
    private Timestamp dateAdded;

    @Column(name = "date_modified")
    private Timestamp dateModified;

    private int balance;

    @Column(name = "last_balance_update")
    private Timestamp lastBalanceUpdate;

    private boolean active;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_type_id", referencedColumnName = "id")
    private AccountType accountType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
