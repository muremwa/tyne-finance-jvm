package com.tyne.finance.core.dto;

import com.tyne.finance.core.models.AccountType;
import lombok.Data;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
public class AccountDTO {
    private BigInteger accountID;

    private String accountProvider;

    private String accountNumber;

    @ReadOnlyProperty
    private Timestamp dateAdded;

    @ReadOnlyProperty
    private Timestamp dateModified;

    private int balance;

    private Timestamp lastBalanceUpdate;

    private boolean active;

    private AccountType accountType;
}
