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

    public void saveLogException(TLogExceptions log) {
        try {
            tLogExceptionsRepository.save(log);
        } catch (Exception e) {
            // Log Exception
        }
    }
}
