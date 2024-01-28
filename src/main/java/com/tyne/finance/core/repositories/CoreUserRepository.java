package com.tyne.finance.core.repositories;

import com.tyne.finance.core.dto.UserCreationRequest;
import com.tyne.finance.core.models.User;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;


@Repository
public interface CoreUserRepository extends JpaRepository<User, BigInteger> {
    User findUserByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    @Transactional
    @Modifying
    @Query(
            value = "INSERT INTO core_user_groups (user_id, group_id) VALUES (:userID, :groupID)",
            nativeQuery = true
    )
    void addUserToGroup(int userID, int groupID);

    @Transactional
    @Modifying
    @Query(value =
            "INSERT INTO core_user " +
                "(password, last_login, is_superuser, username, first_name, last_name, email, is_staff, is_active, date_joined, currency_id) " +
            "VALUES " +
                "(:password, :date, 0, :#{#request.username}, :#{#request.firstName}, :#{#request.lastName}, :#{#request.emailAddress}, 0, 1, :date, :currency)",
            nativeQuery = true
    )
    void createNewUser(@Param("request") UserCreationRequest request, @Param("password") String password, @Param("date") String date, @Param("currency") int currency);
}
