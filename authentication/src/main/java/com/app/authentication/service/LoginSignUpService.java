package com.app.authentication.service;

import com.app.authentication.common.CommonReturn;
import com.app.authentication.common.DbWorker;
import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.jwt.Jwt;
import com.app.authentication.model.JwtUserDetails;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.repository.TMstUserRepository;
import com.app.authentication.security.EncryptionDecryption;
import com.app.authentication.signature.I_LoginSignUpService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Component
public class LoginSignUpService implements I_LoginSignUpService {
    @Autowired
    private TMstUserRepository tmstUserRepository;
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private Jwt jwt;

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
            log("getAllUsers()",e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<TMstUser> getUserById(Long id) {
        try {
            return tmstUserRepository.findById(id);
        } catch (Exception e) {
            log("getUserById()",e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public int alreadyRegistered(String email) {
        try {
            sql_string = "SELECT * FROM t_mst_user WHERE email = :value1";
            List<Object> params = List.of(email);

            return ((TMstUser)dbWorker.getDataset(sql_string, entityManager, params, TMstUser.class).getSingleResult() != null) ? 1 : 0;
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            log("alreadyRegistered()",e.getMessage());
            return 2;
        }
    }

    @Override
    public CommonReturn<Boolean> saveUser(TMstUserModel new_user) {
        try {
            int is_already_registered = alreadyRegistered(new_user.getEmail());

            if(is_already_registered == 1){
                return CommonReturn.error(400,"The email address [" + new_user.getEmail() + "] is already registered.");
            }else if(is_already_registered == 2){
                return CommonReturn.error(400,"Internal Server Error.");
            }

            TMstUser user_entity = new TMstUser(new_user.getFirst_name(),new_user.getLast_name(),new_user.getEmail(),new_user.getPassword());
            tmstUserRepository.save(user_entity);

            return CommonReturn.success("Sign-Up is successful. Please Login now.",true);
        } catch (Exception e) {
            log("saveUser()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @Override
    public CommonReturn<TMstUserModel> validateUser(TMstUserModel new_user){
        try {
            sql_string = "SELECT * FROM t_mst_user WHERE email = :value1 and password = :value2";
            List<Object> params = List.of(new_user.getEmail(), new_user.getPassword());

            TMstUser validated_user = (TMstUser)dbWorker.getDataset(sql_string, entityManager, params, TMstUser.class).getSingleResult();

            if(validated_user!=null){
                if(new_user.getPassword().equals(validated_user.getPassword()) && encryptionDecryption.Decrypt(new_user.getPassword()).equals(encryptionDecryption.Decrypt(validated_user.getPassword()))){
                    JwtUserDetails jwt_user_details = new JwtUserDetails(validated_user.getId(),validated_user.getEmail(),validated_user.getIs_subscribed(),validated_user.getIs_active());
                    String jwt_token = jwt.generateToken(jwt_user_details);

                    TMstUserModel returned_user = new TMstUserModel(validated_user.getFirst_name(),validated_user.getLast_name(),validated_user.getIs_subscribed(),validated_user.getIs_active(),jwt_token,validated_user.getTrans_datetime());
                    return CommonReturn.success("Login is successful.",returned_user);
                }else{
                    return CommonReturn.error(401,"Incorrect Username or Password.");
                }
            }else{
                return CommonReturn.error(401,"Incorrect Username or Password.");
            }
        } catch (NoResultException e) {
            return CommonReturn.error(401,"Incorrect Username or Password.");
        } catch (Exception e) {
            log("validateUser()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            tmstUserRepository.deleteById(id);

            return true;
        } catch (Exception e) {
            log("deleteUser()",e.getMessage());
            return false;
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
