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

        String s = PasswordHashAsmCrypt.passwordHash(str, slat);
        Assertions.assertNotNull(s);

        s = PasswordHashAsmCrypt.passwordHash("", slat);
        Assertions.assertNotNull(s);

        s = PasswordHashAsmCrypt.passwordHash(str, slat2);
        Assertions.assertEquals("", s);

        s = PasswordHashAsmCrypt.passwordHash("", slat, 0);
        Assertions.assertEquals("", s);

    }
}