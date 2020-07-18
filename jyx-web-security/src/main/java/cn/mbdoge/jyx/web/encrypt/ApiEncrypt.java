package cn.mbdoge.jyx.web.encrypt;

/**
 * @author jyx
 */
public interface ApiEncrypt {
    /**
     * 加密字符串
     * @param plainText 需要加密的字符串
     * @return 加密后的字符串
     */
    String encrypt(String plainText);

    /**
     * 将对象序列化后进行加密
     * @param obj 需要加密的对象
     * @return 加密后的字符串
     */
    String encryptObj(Object obj);

    /**
     * 将字符串解密
     * @param content 需要加密的对象
     * @return 加密后的字符串
     */
    String decrypt(String content);

    /**
     * 将字符串解密得到对象
     * @param content 需要解密的字符串
     * @param cla 预期类型
     * @return 解密后得到对象
     */
    <T> T decrypt(String content, Class<T> cla);
}
