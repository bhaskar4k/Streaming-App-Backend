package com.app.authentication.signature;
import com.app.authentication.common.CommonReturn;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.JwtUserDetails;
import com.app.authentication.model.TMstUserModel;
import com.app.authentication.model.ValidatedUserDetails;

import java.util.List;
import java.util.Optional;

public interface I_LoginSignUpService {

    public List<TMstUser> getAllUsers();

    public Optional<TMstUser> getUserById(Long id);

    public int alreadyRegistered(String email);

    public CommonReturn<Boolean> saveUser(TMstUserModel new_user);

    public boolean deleteUser(Long id);

    public CommonReturn<ValidatedUserDetails> validateUser(TMstUserModel new_user);

    public CommonReturn<Long> getMstUserIdFromJWT(String JWT);

    public CommonReturn<String> getEmailFromJWT(String JWT);

    public CommonReturn<Integer> getIsSubscribedFromJWT(String JWT);

    public CommonReturn<String> getIpAddressFromJWT(String JWT);

    public CommonReturn<Long> getDeviceCountFromJWT(String JWT);
}