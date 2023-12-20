package com.tyne.finance.core.repositories;

import com.tyne.finance.core.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;


@Repository
public interface CoreUserRepository extends JpaRepository<User, BigInteger> {
    public User findUserByUsername(String username);
}
