package com.example.demo.domain.entity.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<User> search(@Param("searchText") String searchText, Pageable pageable);
}