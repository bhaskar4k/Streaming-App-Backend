package com.app.streaming.service;


import com.app.streaming.entity.TLogExceptions;
import com.app.streaming.repository.TLogExceptionsRepository;
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
