package com.tyne.finance.core.repositories;


import com.tyne.finance.core.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface GroupRepository extends JpaRepository<Group, BigInteger> {}
