package com.andrewn.userapi.repository;

import com.andrewn.userapi.model.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> findByFirstName(String firstName, Pageable pageable);
    Page<User> findByLastName(String lastName, Pageable pageable);
    Page<User> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);
}
