package com.app.authentication.signature;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.TMstUserModel;

import java.util.List;
import java.util.Optional;

public interface I_LoginSignUpService {

    public List<TMstUser> getAllUsers();

    public Optional<TMstUser> getUserById(Long id);

    public boolean alreadyRegistered(String email);

    public TMstUser saveUser(TMstUserModel new_user);

    public boolean deleteUser(Long id);

    public TMstUser validateUser(TMstUserModel new_user);
}