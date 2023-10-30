package com.tyne.finance.core.repositories;


import com.tyne.finance.core.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface CoreAccountRepository extends JpaRepository<Account, BigInteger> {}
