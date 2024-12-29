package com.app.authentication.signature;
import com.app.authentication.common.CommonReturn;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.TMstUserModel;

import java.util.List;
import java.util.Optional;

public interface I_LoginSignUpService {

    public List<TMstUser> getAllUsers();

    public Optional<TMstUser> getUserById(Long id);

    public int alreadyRegistered(String email);

    public CommonReturn<Boolean> saveUser(TMstUserModel new_user);

    public boolean deleteUser(Long id);

    public CommonReturn<TMstUserModel> validateUser(TMstUserModel new_user);
}