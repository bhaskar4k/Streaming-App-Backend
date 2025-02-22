package com.app.dashboard.repository;


import com.app.dashboard.entity.TLogExceptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TLogExceptionsRepository extends JpaRepository<TLogExceptions, Long> { }
