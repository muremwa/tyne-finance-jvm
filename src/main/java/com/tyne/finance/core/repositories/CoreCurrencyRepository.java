package com.tyne.finance.core.repositories;

import com.tyne.finance.core.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface CoreCurrencyRepository extends JpaRepository<Currency, BigInteger> {
    public Optional<Currency> findCurrencyByCode(String code);
}
