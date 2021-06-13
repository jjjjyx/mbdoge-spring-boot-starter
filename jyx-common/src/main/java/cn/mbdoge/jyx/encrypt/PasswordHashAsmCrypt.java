package cn.mbdoge.jyx.encrypt;

import lombok.SneakyThrows;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * // hmac
 * @author jyx
 */
public final class PasswordHashAsmCrypt {

    private static final Pattern HEX_PATTERN= Pattern.compile(".{2}");
    /**
     * 加密前缀 v01
      */
    private static final byte[] PREFIX = new byte[]{ 118, 48, 49 };

    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    public static String passwordHash (String value, char[] salt) {
        return passwordHash(value, salt, 50000);
    }

    public static String passwordHash (String value, char[] salt, int iterations) {
        return passwordHash(value, new String(salt).getBytes(StandardCharsets.UTF_8), iterations);
    }

    public static String passwordHash (String value, byte[] salt, int iterations) {

        Objects.requireNonNull(value);
        Objects.requireNonNull(salt);

        byte[] hash;
        try {
            PBEKeySpec spec = new PBEKeySpec(value.toCharArray(), salt , iterations, 32 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            hash = skf.generateSecret(spec).getEncoded();

            String hex = String.format("%06x", iterations);
            hex = hex.substring(hex.length() - 6);

            byte[] iterationsHex = new byte[3];
            Matcher matcher = HEX_PATTERN.matcher(hex);
            int i =0;

            while ( matcher.find() ) {
                iterationsHex[i] = (byte) Integer.parseInt(matcher.group(0), 16);
                i++;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(PREFIX);
            outputStream.write(salt);

            outputStream.write(iterationsHex);
            outputStream.write(hash);
            return ENCODER.encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }


    // public static String passwordHash (String value) {
    //     return passwordHashAsmCrypto(value, SALT, ITERATIONS);
    // }

}
