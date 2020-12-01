package cn.mbdoge.jyx.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Objects;


/**
 * @author jyx
 */
public final class AesEncrypt {
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    public static String encrypt(String plainText, String key) throws GeneralSecurityException {
        Objects.requireNonNull(plainText);
        Objects.requireNonNull(key);
        byte[] clean = plainText.getBytes(StandardCharsets.UTF_8);

        // Generating IV.
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        MessageDigest digest = MessageDigest.getInstance("SHA-512");

        digest.update(key.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = new byte[16];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Encrypt.
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(clean);

        return ENCODER.encodeToString(encrypted);
    }


    public static String decrypt(String content, String key) throws GeneralSecurityException {
        Objects.requireNonNull(content);
        Objects.requireNonNull(key);

        int ivSize = 16;
        int keySize = 16;

        byte[] encryptedIvTextBytes = DECODER.decode(content);

        // Extract IV.
        byte[] iv = new byte[ivSize];
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Hash key.
        byte[] keyBytes = new byte[keySize];
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.update(key.getBytes());
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // Decrypt.
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] decrypted = cipherDecrypt.doFinal(encryptedIvTextBytes);

        return new String(decrypted);
    }

//    private static String bytesToHex(byte[] hashInBytes) {
//
//        StringBuilder sb = new StringBuilder();
//        for (byte hashInByte : hashInBytes) {
//            sb.append(Integer.toString((hashInByte & 0xff) + 0x100, 16).substring(1));
//        }
//        return sb.toString();
//
//    }
}
