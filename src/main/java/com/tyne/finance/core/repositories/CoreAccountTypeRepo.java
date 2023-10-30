package com.tyne.finance.core.repositories;

import com.tyne.finance.core.models.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface CoreAccountTypeRepo extends JpaRepository<AccountType, BigInteger> {}
