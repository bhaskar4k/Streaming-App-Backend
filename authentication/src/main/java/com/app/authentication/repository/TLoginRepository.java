package com.app.authentication.repository;

import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.entity.TLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TLoginRepository extends JpaRepository<TLogin, Long> { }