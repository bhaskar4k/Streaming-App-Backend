package com.app.authentication.jwt;

import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.environment.Environment;
import com.app.authentication.model.JwtUserDetails;
import com.app.authentication.service.LogExceptionsService;
import com.fasterxml.jackson.databind.ObjectMapper;  // Jackson library for serialization
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class Jwt {
    private static Environment environment = new Environment();
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final SecretKey SECRET_KEY = new SecretKeySpec(environment.getSecretKey().getBytes(), "HmacSHA512");
    private final Long expirationMillis = environment.getJwtExpireTime();


    public String generateToken(Object userObject) {
        try {
            Map<String, Object> claims = new HashMap<>();
            String userJson = objectMapper.writeValueAsString(userObject);
            return createToken(claims, userJson);
        } catch (Exception e) {
            return null;
        }
    }

    private String createToken(Map<String, Object> claims, String userJson) {
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userJson)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                    .signWith(SECRET_KEY)
                    .compact();
        } catch (Exception e) {
            return null;
        }
    }

    public String extractSubject(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (JwtException e) {
            return null;
        }
    }

    public Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (JwtException e) {
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (JwtException e) {
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            return null;
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isAuthenticated(String token){
        try {
            boolean is_authenticated = true;

            if (StringUtils.hasText(token)) {
                try {
                    if (validateToken(token)) {
                        String subject = extractSubject(token);

                        if (StringUtils.hasText(subject)) {
                            JwtUserDetails extractedUserObject = objectMapper.readValue(subject, JwtUserDetails.class);

                            if (extractedUserObject == null) {
                                is_authenticated = false;
                            }
                        } else {
                            is_authenticated = false;
                        }
                    } else {
                        is_authenticated = false;
                    }
                } catch (Exception e) {
                    is_authenticated = false;
                }
            }

            return is_authenticated;
        } catch (Exception e) {
            return false;
        }
    }
}