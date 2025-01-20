package com.app.upload.repository;


import com.app.upload.entity.TLogExceptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TLogExceptionsRepository extends JpaRepository<TLogExceptions, Long> { }
