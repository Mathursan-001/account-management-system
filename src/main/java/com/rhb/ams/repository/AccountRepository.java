package com.rhb.ams.repository;

import com.rhb.ams.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find all accounts belonging to a specific customer
     *
     * @param customerId The ID of the customer
     * @return List of accounts for the customer
     */
    @Query("SELECT a FROM Account a WHERE a.customer.id = :customerId")
    List<Account> findByCustomerId(@Param("customerId") Long customerId);
}
