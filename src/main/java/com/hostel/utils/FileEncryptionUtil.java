package com.hostel.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class FileEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128; // in bits

    @Value("${file.encryption.key}")
    private String encryptionKeyBase64;

    public byte[] encrypt(byte[] data) throws Exception {
        byte[] key = Base64.getDecoder().decode(encryptionKeyBase64);
        SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);

        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);

        byte[] encryptedData = cipher.doFinal(data);

        // Concatenate IV and Encrypted Data: [IV (12 bytes)] + [Encrypted Data]
        byte[] encryptedFileBytes = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, encryptedFileBytes, 0, iv.length);
        System.arraycopy(encryptedData, 0, encryptedFileBytes, iv.length, encryptedData.length);

        return encryptedFileBytes;
    }

    public byte[] decrypt(byte[] encryptedFileBytes) throws Exception {
        byte[] key = Base64.getDecoder().decode(encryptionKeyBase64);
        SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);

        // Extract IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(encryptedFileBytes, 0, iv, 0, iv.length);

        // Extract Encrypted Data
        byte[] encryptedData = new byte[encryptedFileBytes.length - iv.length];
        System.arraycopy(encryptedFileBytes, iv.length, encryptedData, 0, encryptedData.length);

        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);

        return cipher.doFinal(encryptedData);
    }
}
