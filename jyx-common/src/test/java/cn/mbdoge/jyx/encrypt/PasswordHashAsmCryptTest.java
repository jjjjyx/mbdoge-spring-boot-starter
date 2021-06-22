package cn.mbdoge.jyx.encrypt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashAsmCryptTest {

    static char[] slat = {'x', 'x'};
    static char[] slat2 = {};


    @Test
    void passwordHash() {

        String str = "pp";
        char[] slat = "Y29udHJvbGxlci5hdXRoQ29udHJvbGxlci5Mb2dpbg==".toCharArray();
        //
        // String s = PasswordHashAsmCrypt.passwordHash(str, slat);
        // Assertions.assertNotNull(s);
        //
        // s = PasswordHashAsmCrypt.passwordHash("", slat);
        // Assertions.assertNotNull(s);
        //
        // s = PasswordHashAsmCrypt.passwordHash(str, slat2);
        // Assertions.assertEquals("", s);
        //
        // s = PasswordHashAsmCrypt.passwordHash("", slat, 0);
        // Assertions.assertEquals("", s);


        String hash = PasswordHashAsmCrypt.passwordHash("9bteSty1", slat, 50000);
        System.out.println("hash = " + hash);

    }
}