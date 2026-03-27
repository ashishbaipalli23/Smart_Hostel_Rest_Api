package com.hostel.utilties;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;


public class JwtKeyGenerator {

    public static void main(String[] args) {

        // Generate secure random key (256-bit for HS256)
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();

        // Encode to Base64 URL safe string (no padding)
        String key = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(keyBytes);

        System.out.println("Generated JWT Secret Key:");
        System.out.println(key);
    }
}