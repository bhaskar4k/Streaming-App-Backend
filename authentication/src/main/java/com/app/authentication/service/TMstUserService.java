package com.app.authentication.service;

import com.app.authentication.entity.TMstUser;
import com.app.authentication.repository.TMstUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Component
public class TMstUserService {
    @Autowired
    private TMstUserRepository tmstUserRepository;

    public List<TMstUser> getAllUser() {
        return tmstUserRepository.findAll();
    }

    public Optional<TMstUser> getUserById(Long id) {
        return tmstUserRepository.findById(id);
    }

    public TMstUser saveUser(TMstUser user) {
        return tmstUserRepository.save(user);
    }

    public void deleteUser(Long id) {
        tmstUserRepository.deleteById(id);
    }
}
