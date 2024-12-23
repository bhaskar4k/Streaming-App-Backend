package com.app.authentication.signature;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.TMstUserModel;

import java.util.List;
import java.util.Optional;

public interface I_LoginSignUpService {

    public List<TMstUser> getAllProducts();

    public Optional<TMstUser> getProductById(Long id);

    public boolean alreadyRegistered(String email);

    public TMstUser saveProduct(TMstUserModel new_user);

    public boolean deleteProduct(Long id);

    public TMstUser validateUser(TMstUserModel new_user);

    public TMstUser getUserDetailsByEmail(String value, String password);
}