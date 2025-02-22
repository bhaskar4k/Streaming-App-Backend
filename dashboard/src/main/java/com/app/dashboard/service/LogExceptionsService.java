package com.app.dashboard.service;


import com.app.dashboard.entity.TLogExceptions;
import com.app.dashboard.repository.TLogExceptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogExceptionsService {
    @Autowired
    private TLogExceptionsRepository tLogExceptionsRepository;

    public void saveLogException(TLogExceptions log) {
        try {
            tLogExceptionsRepository.save(log);
        } catch (Exception e) {
        }
    }
}
