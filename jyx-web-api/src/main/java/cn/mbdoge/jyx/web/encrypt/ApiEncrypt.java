package cn.mbdoge.jyx.web.encrypt;

public interface ApiEncrypt {
    String encrypt(String plainText);

    String encryptObj(Object obj);

    String decrypt(String content);

    <T> T decrypt(String content, Class<T> cla);
}
