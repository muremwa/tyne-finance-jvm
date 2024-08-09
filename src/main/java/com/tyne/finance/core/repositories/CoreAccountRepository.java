package com.tyne.finance.core.repositories;


import com.tyne.finance.core.models.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CoreAccountRepository extends JpaRepository<Account, BigInteger> {
    List<Account> findAccountsByUserUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "UPDATE core_account SET active = :status WHERE id = :account", nativeQuery = true)
    void updateAccountActiveStatus(@Param("account") BigInteger accountId, @Param("status") int status);
}
