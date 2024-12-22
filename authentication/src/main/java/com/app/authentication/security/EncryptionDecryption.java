package com.app.authentication.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionDecryption {
    private static final String SECRET_KEY = "12345678901234567890123456789012"; // 32-byte key
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // Method to encrypt the password
    public String encrypt(String password) throws Exception {
        // Generate the IV (Initialization Vector)
        byte[] iv = new byte[16]; // AES requires 16-byte IV
        new java.security.SecureRandom().nextBytes(iv);

        // Create Cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        SecretKey key = getKeyFromPassword(SECRET_KEY);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

        // Encrypt the password
        byte[] encryptedPassword = cipher.doFinal(password.getBytes("UTF-8"));

        // Combine IV and encrypted password and encode in Base64
        String ivBase64 = Base64.getEncoder().encodeToString(iv);
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedPassword);

        return ivBase64 + ":" + encryptedBase64;
    }

    // Method to decrypt the password
    public String decrypt(String encryptedPassword) throws Exception {
        String[] parts = encryptedPassword.split(":");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Invalid encrypted password format.");
        }

        String ivBase64 = parts[0];
        String encryptedBase64 = parts[1];

        // Decode IV and encrypted password from Base64
        byte[] iv = Base64.getDecoder().decode(ivBase64);
        byte[] encrypted = Base64.getDecoder().decode(encryptedBase64);

        // Create Cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        SecretKey key = getKeyFromPassword(SECRET_KEY);

        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        // Decrypt the password
        byte[] decryptedPassword = cipher.doFinal(encrypted);

        return new String(decryptedPassword, "UTF-8");
    }

    // Helper method to generate SecretKey from the password
    private SecretKey getKeyFromPassword(String password) throws NoSuchAlgorithmException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            byte[] key = Arrays.copyOf(hash, 32); // AES-256 requires a 32-byte key
            return new javax.crypto.spec.SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new NoSuchAlgorithmException("Error generating key from password", e);
        }
    }
}