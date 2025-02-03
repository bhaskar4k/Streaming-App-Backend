package com.app.processing.repository;


import com.app.processing.entity.TLogExceptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TLogExceptionsRepository extends JpaRepository<TLogExceptions, Long> { }
