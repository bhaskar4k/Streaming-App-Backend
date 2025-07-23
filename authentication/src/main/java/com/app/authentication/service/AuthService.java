package com.app.authentication.service;

import com.app.authentication.common.CommonReturn;
import com.app.authentication.common.DbWorker;
import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.entity.TLogin;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.enums.UIEnum;
import com.app.authentication.environment.Environment;
import com.app.authentication.jwt.Jwt;
import com.app.authentication.model.JwtUserDetails;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.repository.TLoginRepository;
import com.app.authentication.security.EncryptionDecryption;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@Component
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
public class AuthService {
    @Autowired
    private TLoginRepository tLoginRepository;
    @Autowired
    private LogExceptionsService logExceptionsService;
    @Autowired
    private Jwt jwt;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    private EncryptionDecryption encryptionDecryption;
    private Environment environment;
    private DbWorker dbWorker;
    private String sql_string;
    List<Object> params;

    @PersistenceContext
    private EntityManager entityManager;

    public AuthService(){
        this.encryptionDecryption=new EncryptionDecryption();
        this.environment=new Environment();
        this.dbWorker=new DbWorker();
    }

    public void emitLogoutMessageIntoWebsocket(Long t_mst_user_id, Long device_number) {
        try {
            String device_endpoint = environment.getDeviceEndpoint(t_mst_user_id,device_number);
            messagingTemplate.convertAndSend("/topic/logout"+device_endpoint, CommonReturn.success("Your account has been logged-in from another device. Logging out....", "logout_"+device_endpoint));
        } catch (Exception e) {
            log("emitLogoutMessageIntoWebsocket()",e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public String generateTokenAndUpdateDB(TMstUserModel new_user, TMstUser validated_user){
        try {
            sql_string = "select count(id) as count from t_login where t_mst_user_id = :value1 and is_active = " + UIEnum.ActivityStatus.ACTIVE.getValue();;
            params = List.of(validated_user.getId());
            Long loggedin_device_number = (Long)dbWorker.getQuery(sql_string, entityManager, params, null).getSingleResult() + 1;

            if(loggedin_device_number > environment.getMaximumLoginDevice()){
                // Generate random integers in range 1 to environment.getMaximum_login_device()
                Random rand = new Random();
                Long removed_device_number = rand.nextLong(environment.getMaximumLoginDevice())+1;

                params = List.of(validated_user.getId(),removed_device_number);
                sql_string = "UPDATE t_login set is_active = " + UIEnum.ActivityStatus.IN_ACTIVE.getValue() +
                             " WHERE t_mst_user_id = :value1 and device_count = :value2 and is_active = " + UIEnum.ActivityStatus.ACTIVE.getValue();
                int updated = dbWorker.getQuery(sql_string, entityManager, params, null).executeUpdate();
                if(updated == 0) return null;

                loggedin_device_number = removed_device_number;
                emitLogoutMessageIntoWebsocket(validated_user.getId(), removed_device_number);
            }else{
                for(Long cur_device_no=1L; cur_device_no<=environment.getMaximumLoginDevice(); cur_device_no++){
                    sql_string = "select count(id) as count from t_login where t_mst_user_id = :value1 and device_count = :value2 and is_active = " + UIEnum.ActivityStatus.ACTIVE.getValue();;
                    params = List.of(validated_user.getId(),cur_device_no);
                    Long count = (Long)dbWorker.getQuery(sql_string, entityManager, params, null).getSingleResult();

                    if(count==0){
                        loggedin_device_number = cur_device_no;
                        break;
                    }
                }
            }

            String full_name = validated_user.getFirst_name() + " " + validated_user.getLast_name();
            JwtUserDetails jwt_user_details = new JwtUserDetails(validated_user.getId(), full_name,validated_user.getEmail(), validated_user.getIs_subscribed(), new_user.getIp_address(), loggedin_device_number);
            String jwt_token = jwt.generateToken(jwt_user_details);

            if(jwt_token != null){
                TLogin login_entity = new TLogin(validated_user.getId(), jwt_token, new_user.getIp_address(), loggedin_device_number, UIEnum.ActivityStatus.ACTIVE.getValue());
                tLoginRepository.save(login_entity);
            }

            return jwt_token;
        } catch (Exception e) {
            log("generateTokenAndUpdateDB()", e.getMessage());
            return null;
        }
    }

    public JwtUserDetails getAuthenticatedUserFromJwt(String JWT){
        try {
            String jwtSubject = jwt.extractSubject(JWT);
            return (JwtUserDetails)objectMapper.readValue(jwtSubject, JwtUserDetails.class);
        } catch (Exception e) {
            log("getAuthenticatedUserFromJwt()",e.getMessage());
            return null;
        }
    }

    public Boolean isJwtAuthenticated(String token){
        try {
            Boolean authenticated = jwt.isAuthenticated(token);

            if(!authenticated) doLogoutForUnauthorizedRequest(token);
            return authenticated;
        } catch (Exception e) {
            log("isJwtAuthenticated()",e.getMessage());
            return null;
        }
    }

    public JwtUserDetails getAuthenticatedUserFromContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof JwtUserDetails) {
                    return (JwtUserDetails) principal;
                }else{
                    return null;
                }
            }else{
                return null;
            }
        } catch (Exception e) {
            log("getAuthenticatedUserFromContext()",e.getMessage());
            return null;
        }
    }

    @Transactional
    public void doLogoutForUnauthorizedRequest(String token){
        try {
            String expiredSubject = Jwt.getSubjectFromExpiredToken(token);
            JwtUserDetails expiredExtractedUserObject = objectMapper.readValue(expiredSubject, JwtUserDetails.class);

            params = List.of(expiredExtractedUserObject.getT_mst_user_id(), expiredExtractedUserObject.getDevice_count());

            sql_string = "UPDATE t_login set is_active = " + UIEnum.ActivityStatus.IN_ACTIVE.getValue() +
                    " WHERE t_mst_user_id = :value1 and device_count = :value2 and is_active = " + UIEnum.ActivityStatus.ACTIVE.getValue();

            dbWorker.getQuery(sql_string, entityManager, params, null).executeUpdate();
        } catch (Exception e) {
            log("doLogoutForUnauthorizedRequest()",e.getMessage());
        }
    }

    @Transactional
    public Boolean do_logout(){
        try {
            JwtUserDetails details = getAuthenticatedUserFromContext();
            params = List.of(details.getT_mst_user_id(),details.getDevice_count());

            sql_string = "UPDATE t_login set is_active = " + UIEnum.ActivityStatus.IN_ACTIVE.getValue() +
                         " WHERE t_mst_user_id = :value1 and device_count = :value2 and is_active = " + UIEnum.ActivityStatus.ACTIVE.getValue();

            int updated = dbWorker.getQuery(sql_string, entityManager, params, null).executeUpdate();

            if(updated==1) return true;
        } catch (Exception e) {
            log("do_logout()",e.getMessage());
            return false;
        }

        return false;
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
