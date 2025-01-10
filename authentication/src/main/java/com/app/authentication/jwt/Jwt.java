package com.app.authentication.jwt;

import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.environment.Environment;
import com.app.authentication.service.LogExceptionsService;
import com.fasterxml.jackson.databind.ObjectMapper;  // Jackson library for serialization
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class Jwt {
    @Autowired
    private LogExceptionsService logExceptionsService;
    private static Environment environment = new Environment();
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final SecretKey SECRET_KEY = new SecretKeySpec(environment.getSecretKey().getBytes(), "HmacSHA512");
    private final long expirationMillis = 1000 * 60 * 60 * 10; // 10 hours


    public String generateToken(Object userObject) {
        try {
            Map<String, Object> claims = new HashMap<>();
            String userJson = objectMapper.writeValueAsString(userObject);
            return createToken(claims, userJson);
        } catch (Exception e) {
            log("generateToken()", e.getMessage());
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
            log("createToken()", e.getMessage());
            return null;
        }
    }

    public String extractSubject(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (JwtException e) {
            log("extractEmail()", e.getMessage());
            return null;
        }
    }

    public Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (JwtException e) {
            log("extractExpiration()", e.getMessage());
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (JwtException e) {
            log("extractClaim()", e.getMessage());
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
            log("extractAllClaims()", e.getMessage());
            return null;
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log("isTokenExpired()", e.getMessage());
            return false;
        }
    }

    public Boolean validateToken(String token, Object userObject) {
        try {
            String userJson = extractSubject(token);
            Object extractedUserObject = objectMapper.readValue(userJson, userObject.getClass());
            return userObject.equals(extractedUserObject) && !isTokenExpired(token);
        } catch (Exception e) {
            log("validateToken()", e.getMessage());
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