package com.app.rabbitmq.repository;


import com.app.rabbitmq.entity.TLogExceptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TLogExceptionsRepository extends JpaRepository<TLogExceptions, Long> { }
