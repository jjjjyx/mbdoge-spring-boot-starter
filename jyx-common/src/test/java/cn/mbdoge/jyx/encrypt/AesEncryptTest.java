package cn.mbdoge.jyx.encrypt;

import cn.mbdoge.jyx.exception.ExceptionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AesEncryptTest {

    String KEY = "xxx";
    String KEY2 = "xxxx";

    @Test
    void encrypt() throws GeneralSecurityException {
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("key","value");
//        map.put("中文","汉字");

        String content = "222";
        String encrypt = AesEncrypt.encrypt(content, KEY);

        Assertions.assertNotEquals(content, encrypt);

        String decrypt = AesEncrypt.decrypt(encrypt, KEY);
        Assertions.assertEquals(content, decrypt);
        // ===========


        content = "汉字";
        encrypt = AesEncrypt.encrypt(content, KEY);
        Assertions.assertNotEquals(content, encrypt);

        decrypt = AesEncrypt.decrypt(encrypt, KEY);
        Assertions.assertEquals(content, decrypt);
        // ===========

        String finalEncrypt = encrypt;
        assertThrows(GeneralSecurityException.class, () -> {
            AesEncrypt.decrypt(finalEncrypt, KEY2);
        });
        // ===========

        Assertions.assertThrows(NullPointerException.class, () -> {
            AesEncrypt.encrypt(null, KEY);
        });

        String finalContent = content;
        Assertions.assertThrows(NullPointerException.class, () -> {
            AesEncrypt.encrypt(finalContent, null);
        });
        // ===========

        Assertions.assertThrows(NullPointerException.class, () -> {
            AesEncrypt.decrypt(null, KEY);
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            AesEncrypt.decrypt(finalContent, null);
        });
    }

    @Test
    void decrypt() {
    }
}