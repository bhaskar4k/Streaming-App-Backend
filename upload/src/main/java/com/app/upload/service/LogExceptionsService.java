package com.app.upload.service;


import com.app.upload.entity.TLogExceptions;
import com.app.upload.repository.TLogExceptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogExceptionsService {
    @Autowired
    private TLogExceptionsRepository tLogExceptionsRepository;

    public boolean saveLogException(TLogExceptions log) {
        try {
            tLogExceptionsRepository.save(log);
            return true;
        } catch (Exception e) {
            // Log Exception
            return false;
        }
    }
}
