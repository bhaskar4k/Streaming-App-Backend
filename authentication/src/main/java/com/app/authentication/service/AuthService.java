package com.app.authentication.service;

import com.app.authentication.common.CommonReturn;
import com.app.authentication.common.DbWorker;
import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.entity.TLogin;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.jwt.Jwt;
import com.app.authentication.model.JwtUserDetails;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.repository.TLoginRepository;
import com.app.authentication.repository.TMstUserRepository;
import com.app.authentication.security.EncryptionDecryption;
import com.app.authentication.signature.I_AuthService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public class AuthService implements I_AuthService {
    @Autowired
    private TLoginRepository tLoginRepository;
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private Jwt jwt;

    private EncryptionDecryption encryptionDecryption;
    private DbWorker dbWorker;
    private String sql_string;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String generateTokenAndUpdateDB(TMstUserModel new_user, TMstUser validated_user){
        try {
            JwtUserDetails jwt_user_details = new JwtUserDetails(validated_user.getId(),validated_user.getEmail(),validated_user.getIs_subscribed(),validated_user.getIs_active());
            String jwt_token = jwt.generateToken(jwt_user_details);

            if(jwt_token!=null){
                //handle same jwt_token not present checking in DB
                //handle max 4 device allowance checking
                TLogin login_entity = new TLogin(validated_user.getId(),jwt_token,new_user.getIp_address());
                tLoginRepository.save(login_entity);
            }

            return jwt_token;
        } catch (Exception e) {
            log("generateTokenAndUpdateDB()",e.getMessage());
            return null;
        }
    }


    private void log(String function_name, String exception_msg){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String full_class_path = stackTraceElements[2].getClassName();
        String class_name = full_class_path.substring(full_class_path.lastIndexOf(".") + 1);

        String full_package_path = full_class_path.substring(0, full_class_path.lastIndexOf("."));
        String package_name = full_package_path.substring(full_package_path.lastIndexOf(".") + 1);

        logExceptionsService.saveLogException(new TLogExceptions(package_name,class_name,function_name,exception_msg));
    }
}
