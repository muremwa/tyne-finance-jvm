package com.tyne.finance.core.dto;

import com.tyne.finance.core.models.Currency;
import lombok.Data;

import java.sql.Timestamp;


@Data
public class UserInformation {
    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private Timestamp lastLogin;

    private Timestamp dateJoined;

    private Currency currency;
}
