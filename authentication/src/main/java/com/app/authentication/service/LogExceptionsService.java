package com.app.authentication.service;


import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.repository.TLogExceptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
public class LogExceptionsService {
    @Autowired
    private TLogExceptionsRepository tLogExceptionsRepository;

    public List<TLogExceptions> getAllLogException() {
        try {
            return tLogExceptionsRepository.findAll();
        } catch (Exception e) {
            // Log Exception
            return null;
        }
    }

    public Optional<TLogExceptions> getLogExceptionById(Long id) {
        try {
            return tLogExceptionsRepository.findById(id);
        } catch (Exception e) {
            // Log Exception
            return Optional.empty();
        }
    }

    public boolean saveLogException(TLogExceptions log) {
        try {
            tLogExceptionsRepository.save(log);
            return true;
        } catch (Exception e) {
            // Log Exception
            return false;
        }
    }

    public boolean deleteLogException(Long id) {
        try {
            tLogExceptionsRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            // Log Exception
            return false;
        }
    }
}
