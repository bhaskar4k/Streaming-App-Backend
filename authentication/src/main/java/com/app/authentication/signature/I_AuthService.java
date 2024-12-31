package com.app.authentication.signature;

import com.app.authentication.common.CommonReturn;
import com.app.authentication.entity.TMstUser;
import com.app.authentication.model.TMstUserModel;

public interface I_AuthService {
    public String generateTokenAndUpdateDB(TMstUserModel new_user, TMstUser validated_user);
}
