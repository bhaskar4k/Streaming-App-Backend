package com.app.authentication.jwt;

import com.app.authentication.entity.TLogExceptions;
import com.app.authentication.service.LogExceptionsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class Jwt {

    @Autowired
    private LogExceptionsService logExceptionsService;

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long expirationMillis = 1000 * 60 * 60 * 10;

    public String generateToken(String userName) {
        try {
            Map<String, Object> claims = new HashMap<>();
            return createToken(claims, userName);
        } catch (Exception e) {
            logExceptionsService.saveLogException(new TLogExceptions("jwt", "Jwt", "generateToken()", e.getMessage()));
            return null;
        }
    }

    private String createToken(Map<String, Object> claims, String userName) {
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userName)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                    .signWith(SECRET_KEY)
                    .compact();
        } catch (Exception e) {
            logExceptionsService.saveLogException(new TLogExceptions("jwt", "Jwt", "createToken()", e.getMessage()));
            return null;
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (JwtException e) {
            logExceptionsService.saveLogException(new TLogExceptions("jwt", "Jwt", "extractClaim()", e.getMessage()));
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY) // Use the securely generated key
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logExceptionsService.saveLogException(new TLogExceptions("jwt", "Jwt", "extractAllClaims()", e.getMessage()));
            return null;
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            logExceptionsService.saveLogException(new TLogExceptions("jwt", "Jwt", "isTokenExpired()", e.getMessage()));
            return true; // Treat as expired if there's an issue
        }
    }

    public Boolean validateToken(String token, String user_email) {
        try {
            final String token_email = extractEmail(token);
            return user_email.equals(token_email) && !isTokenExpired(token);
        } catch (Exception e) {
            logExceptionsService.saveLogException(new TLogExceptions("jwt", "Jwt", "validateToken()", e.getMessage()));
            return false;
        }
    }
}
