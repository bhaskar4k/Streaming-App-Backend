package com.app.authentication.service;

import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.repository.TMstUserRepository;
import com.app.authentication.security.EncryptionDecryption;
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
    private TMstUser user_entity;
    private EncryptionDecryption encryptionDecryption;

    public TMstUserService(){
        this.encryptionDecryption=new EncryptionDecryption();
    }

    public List<TMstUser> getAllProducts() {
        return tmstUserRepository.findAll();
    }

    public Optional<TMstUser> getProductById(Long id) {
        return tmstUserRepository.findById(id);
    }

    public boolean alreadyRegistered(String email){
        return tmstUserRepository.existsByEmail(email);
    }

    public TMstUser saveProduct(TMstUserModel new_user) {
        user_entity = new TMstUser(new_user.getFirst_name(),new_user.getLast_name(),new_user.getEmail(),new_user.getPassword());
        tmstUserRepository.save(user_entity);
        return user_entity;
    }

    public void deleteProduct(Long id) {
        tmstUserRepository.deleteById(id);
    }

    public boolean validateUser(TMstUserModel new_user) throws Exception {
        String temp = encryptionDecryption.decrypt(new_user.getPassword());
        String temp1=temp;
        System.out.println(temp);

        return true;
    }
}
