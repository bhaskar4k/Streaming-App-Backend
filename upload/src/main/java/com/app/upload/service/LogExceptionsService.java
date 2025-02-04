package com.app.upload.service;


import com.app.upload.entity.TLogExceptions;
import com.app.upload.repository.TLogExceptionsRepository;
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
