package com.app.authentication.service;

import com.app.authentication.entity.TMstUser;
import com.app.authentication.repository.TMstUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class TMstUserService {
    @Autowired
    private TMstUserRepository tmstUserRepository;

//    public List<T_Mst_User> getAllProducts() {
//        return tmstUserRepository.getAll();
//    }
//
//    public Optional<T_Mst_User> getProductById(Long id) {
//        return tmstUserRepository.getProductById(id);
//    }

    public TMstUser saveProduct(TMstUser user) {
        return tmstUserRepository.save(user);
    }

//    public void deleteProduct(Long id) {
//        tmstUserRepository.deleteProduct(id);
//    }
}
