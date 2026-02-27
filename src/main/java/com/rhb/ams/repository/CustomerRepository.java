package com.rhb.ams.repository;

import com.rhb.ams.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Search customers by name, fromDate, and toDate with pagination
     *
     * @param name       Filter by customer name (supports partial matching)
     * @param fromDate   Filter customers created from this date
     * @param toDate     Filter customers created until this date
     * @param pageable   Pagination information
     * @return Page of customers matching the criteria
     */
    @Query("SELECT c FROM Customer c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:fromDate IS NULL OR c.createdAt >= :fromDate) AND " +
            "(:toDate IS NULL OR c.createdAt <= :toDate)")
    Page<Customer> searchCustomers(
            @Param("name") String name,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );
}
