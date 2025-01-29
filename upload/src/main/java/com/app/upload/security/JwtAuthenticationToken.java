package com.app.upload.security;


import com.app.upload.model.JwtUserDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Custom authentication token class for JWT authentication.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtUserDetails principal; // User details
    private final Object credentials; // Token or password, can be null

    /**
     * Constructor for unauthenticated token (before validation).
     *
     * @param principal   the principal (user details)
     * @param credentials the credentials (e.g., JWT token or password)
     */
    public JwtAuthenticationToken(JwtUserDetails principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    /**
     * Constructor for authenticated token (after validation).
     *
     * @param principal   the principal (user details)
     * @param credentials the credentials (e.g., JWT token or password)
     * @param authorities the authorities granted to the principal
     */
    public JwtAuthenticationToken(JwtUserDetails principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
