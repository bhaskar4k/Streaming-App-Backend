package com.app.authentication.security;

import com.app.authentication.environment.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


public class EncryptionDecryption {
    private String privateKey1;
    private String privateKey2;
    private String encryptionPadding;
    private List<Integer> encryptionNumber;
    private Environment environment;

    public EncryptionDecryption() {
        this.environment = new Environment();
        this.privateKey1 = environment.getPrivateKey1();
        this.privateKey2 = environment.getPrivateKey2();
        this.encryptionPadding = environment.getEncryptionPadding();
        this.encryptionNumber = environment.getEncryptionNumber();
    }

    private String Padding(String password, int padSize) {
        StringBuilder encryptedPassword = new StringBuilder();
        int encryptionPaddingLength = encryptionPadding.length();
        int encryptionNumberLength = encryptionNumber.size();

        for (int i = 0; i < padSize; i++) {
            int XORedCharCode = password.charAt(i % password.length()) ^ encryptionPadding.charAt(i % encryptionPaddingLength);
            XORedCharCode *= encryptionNumber.get(i % encryptionNumberLength);
            encryptedPassword.append(XORedCharCode).append(".");
        }

        return encryptedPassword.toString();
    }

    public String Encrypt(String password) {
        StringBuilder encryptedPassword = new StringBuilder();
        int j = 0;
        int privateKeyLength1 = privateKey1.length();
        int privateKeyLength2 = privateKey2.length();
        int encryptionNumberLength = encryptionNumber.size();

        for (int i = 0; i < password.length(); i++) {
            int XORedCharCode;
            if (i % 2 == 0) {
                XORedCharCode = password.charAt(i) ^ privateKey1.charAt(j++ % privateKeyLength1);
            } else {
                XORedCharCode = password.charAt(i) ^ privateKey2.charAt(j++ % privateKeyLength2);
            }
            XORedCharCode *= encryptionNumber.get(i % encryptionNumberLength);
            encryptedPassword.append(XORedCharCode).append(".");
        }

        int padSize = 99 - (2 * password.length());
        encryptedPassword.append(Padding(password, padSize));

        for (int i = 0; i < password.length(); i++) {
            int XORedCharCode;
            if (i % 2 == 0) {
                XORedCharCode = password.charAt(i) ^ privateKey2.charAt(j++ % privateKeyLength2);
            } else {
                XORedCharCode = password.charAt(i) ^ privateKey1.charAt(j++ % privateKeyLength1);
            }
            XORedCharCode *= encryptionNumber.get(i % encryptionNumberLength);
            encryptedPassword.append(XORedCharCode).append(".");
        }

        String[] parts = encryptedPassword.toString().split("\\.");
        int firstEncryptedCharVal = Integer.parseInt(parts[0]);
        encryptedPassword.append(password.length() * encryptionNumber.get(encryptionNumberLength - 1) * firstEncryptedCharVal).append(".");

        return Base64.getEncoder().encodeToString(encryptedPassword.toString().getBytes());
    }

    public String Decrypt(String encryptedPassword) {
        encryptedPassword = new String(Base64.getDecoder().decode(encryptedPassword));

        StringBuilder decryptedPassword = new StringBuilder();
        String[] encryptedParts = encryptedPassword.split("\\.");
        List<Integer> parsedParts = new ArrayList<>();

        for (String part : encryptedParts) {
            if (!part.isEmpty()) {
                parsedParts.add(Integer.parseInt(part));
            }
        }

        int encryptionNumberLength = encryptionNumber.size();
        int privateKeyLength1 = privateKey1.length();
        int privateKeyLength2 = privateKey2.length();
        int j = 0;

        int originalPasswordLength = parsedParts.get(parsedParts.size() - 1) / encryptionNumber.get(encryptionNumberLength - 1) / parsedParts.get(0);

        for (int i = 0; i < originalPasswordLength; i++) {
            int XORedCharCode = parsedParts.get(i);
            XORedCharCode /= encryptionNumber.get(i % encryptionNumberLength);

            if (i % 2 == 0) {
                decryptedPassword.append((char) (XORedCharCode ^ privateKey1.charAt(j++ % privateKeyLength1)));
            } else {
                decryptedPassword.append((char) (XORedCharCode ^ privateKey2.charAt(j++ % privateKeyLength2)));
            }
        }

        return decryptedPassword.toString();
    }
}