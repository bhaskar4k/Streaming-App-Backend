package com.app.authentication.service;

import com.app.authentication.bloomfilter.BloomFilter;
import com.app.authentication.common.CommonReturn;
import com.app.authentication.common.DbWorker;
import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.environment.Environment;
import com.app.authentication.jwt.Jwt;
import com.app.authentication.model.JwtUserDetails;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.model.ValidatedUserDetails;
import com.app.authentication.repository.TMstUserRepository;
import com.app.authentication.security.EncryptionDecryption;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;
import java.util.Optional;

import static com.app.authentication.AuthenticationApplication.BloomFilter;


@Service
@Component
public class LoginSignUpService {
    @Autowired
    private TMstUserRepository tmstUserRepository;
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private AuthService authService;
    @Autowired
    private StringRedisTemplate Redis;

    private EncryptionDecryption encryptionDecryption;
    private Environment environment;
    private DbWorker dbWorker;
    private String sql_string;
    List<Object> params;

    @PersistenceContext
    private EntityManager entityManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    public LoginSignUpService(){
        this.encryptionDecryption=new EncryptionDecryption();
        this.environment=new Environment();
        this.dbWorker=new DbWorker();
    }

    public List<TMstUser> getAllUsers() {
        try {
            return tmstUserRepository.findAll();
        } catch (Exception e) {
            log("getAllUsers()",e.getMessage());
            return null;
        }
    }

    public Optional<TMstUser> getUserById(Long id) {
        try {
            return tmstUserRepository.findById(id);
        } catch (Exception e) {
            log("getUserById()",e.getMessage());
            return Optional.empty();
        }
    }

    public int alreadyRegistered(String email) {
        try {
            boolean mightContain = BloomFilter.mightContain(email);
            if(!mightContain) return 0;

            Boolean existsInRedis = Redis.hasKey(email);
            if (Boolean.TRUE.equals(existsInRedis)) return 1;

            sql_string = "SELECT * FROM t_mst_user WHERE email = :value1";
            params = List.of(email);

            return ((TMstUser)dbWorker.getQuery(sql_string, entityManager, params, TMstUser.class).getSingleResult() != null) ? 1 : 0;
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            log("alreadyRegistered()",e.getMessage());
            return 2;
        }
    }

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

            BloomFilter.add(new_user.getEmail());
            Redis.opsForValue().set(new_user.getEmail(), new_user.getEmail());

            return CommonReturn.success("Sign-Up is successful. Please Login now.",true);
        } catch (Exception e) {
            log("saveUser()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    public CommonReturn<ValidatedUserDetails> validateUser(TMstUserModel new_user){
        try {
            sql_string = "SELECT * FROM t_mst_user WHERE email = :value1 and password = :value2";
            params = List.of(new_user.getEmail(), new_user.getPassword());

            TMstUser validated_user = (TMstUser)dbWorker.getQuery(sql_string, entityManager, params, TMstUser.class).getSingleResult();

            if(validated_user!=null){
                if(new_user.getPassword().equals(validated_user.getPassword()) && encryptionDecryption.Decrypt(new_user.getPassword()).equals(encryptionDecryption.Decrypt(validated_user.getPassword()))){
                    String jwt_token = authService.generateTokenAndUpdateDB(new_user,validated_user);

                    if(jwt_token!=null){
                        Long t_mst_user_id = authService.getAuthenticatedUserFromJwt(jwt_token).getT_mst_user_id();
                        Long device_count = authService.getAuthenticatedUserFromJwt(jwt_token).getDevice_count();
                        String device_endpoint = environment.getDeviceEndpoint(t_mst_user_id,device_count);

                        return CommonReturn.success("Login is successful.",new ValidatedUserDetails(device_endpoint,jwt_token));
                    }else{
                        return CommonReturn.error(401,"Auth token generation failed.");
                    }
                }else{
                    return CommonReturn.error(401,"Incorrect Username or Password/User doesn't exist.");
                }
            }else{
                return CommonReturn.error(401,"Incorrect Username or Password/User doesn't exist.");
            }
        } catch (NoResultException e) {
            return CommonReturn.error(401,"Incorrect Username or Password/User doesn't exist.");
        } catch (Exception e) {
            log("validateUser()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

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
