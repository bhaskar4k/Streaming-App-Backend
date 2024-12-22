package com.app.authentication.repository;

import com.app.authentication.entity.TMstUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TMstUserRepository extends JpaRepository<TMstUser, Long> {
    boolean existsByEmail(String email);
}
