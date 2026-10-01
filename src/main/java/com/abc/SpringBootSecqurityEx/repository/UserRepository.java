package com.abc.SpringBootSecqurityEx.repository;


import com.abc.SpringBootSecqurityEx.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);


    Page<User> findByUserNameContainingIgnoreCaseOrUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String searchTerm, String searchTerm1, String searchTerm2, String searchTerm3, Pageable pageable);

    boolean existsByUserNameIgnoreCase(String userName);

    boolean existsByEmailIgnoreCase(String email);

    long countByEnabledTrue();
}
