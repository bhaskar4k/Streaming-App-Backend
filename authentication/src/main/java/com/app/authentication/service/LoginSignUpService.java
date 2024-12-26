package com.app.authentication.service;

import com.app.authentication.common.DbWorker;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.repository.TMstUserRepository;
import com.app.authentication.security.EncryptionDecryption;
import com.app.authentication.signature.I_LoginSignUpService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@Component
public class LoginSignUpService implements I_LoginSignUpService {
    @Autowired
    private TMstUserRepository tmstUserRepository;
    private TMstUser user_entity;
    private EncryptionDecryption encryptionDecryption;
    private DbWorker dbWorker;
    private String sql_string;

    @PersistenceContext
    private EntityManager entityManager;

    public LoginSignUpService(){
        this.encryptionDecryption=new EncryptionDecryption();
        this.dbWorker=new DbWorker();
    }

    @Override
    public List<TMstUser> getAllUsers() {
        try {
            return tmstUserRepository.findAll();
        } catch (Exception e) {
            // Log Exception
            return null;
        }
    }

    @Override
    public Optional<TMstUser> getUserById(Long id) {
        try {
            return tmstUserRepository.findById(id);
        } catch (Exception e) {
            // Log Exception
            return Optional.empty();
        }
    }

    @Override
    public boolean alreadyRegistered(String email) {
        try {
            sql_string = "SELECT * FROM t_mst_user WHERE email = :value1";
            List<Object> params = List.of(email);

            return ((TMstUser)dbWorker.getDataset(sql_string, entityManager, params, TMstUser.class).getSingleResult() != null);
        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            // Log Exception
            return false;
        }
    }

    @Override
    public TMstUser saveUser(TMstUserModel new_user) {
        try {
            user_entity = new TMstUser(new_user.getFirst_name(),new_user.getLast_name(),new_user.getEmail(),new_user.getPassword());
            tmstUserRepository.save(user_entity);

            return user_entity;
        } catch (Exception e) {
            // Log Exception
            return null;
        }
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            tmstUserRepository.deleteById(id);

            return true;
        } catch (Exception e) {
            // Log Exception
            return false;
        }
    }

    @Override
    public TMstUser validateUser(TMstUserModel new_user){
        try {
            sql_string = "SELECT * FROM t_mst_user WHERE email = :value1 and password = :value2";
            List<Object> params = List.of(new_user.getEmail(), new_user.getPassword());

            return (TMstUser)dbWorker.getDataset(sql_string, entityManager, params, TMstUser.class).getSingleResult();
        } catch (Exception e) {
            // Log Exception
            return null;
        }
    }
}
