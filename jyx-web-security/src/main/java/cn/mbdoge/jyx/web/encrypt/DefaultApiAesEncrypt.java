package cn.mbdoge.jyx.web.encrypt;

import cn.mbdoge.jyx.encrypt.AesEncrypt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.security.GeneralSecurityException;
import java.util.Objects;

@Slf4j
public class DefaultApiAesEncrypt implements ApiEncrypt {
    private final String secret;
    private final ObjectMapper objectMapper;

    public DefaultApiAesEncrypt(ApiEncryptProperties apiEncryptProperties, ObjectMapper objectMapper) {
        secret = apiEncryptProperties.getSecret();
        this.objectMapper = objectMapper;
    }

    @Override
    public String encrypt(String plainText) {
        try {
            return AesEncrypt.encrypt(plainText, secret);
        } catch (GeneralSecurityException e) {
            log.warn("加密失败 reason: {}", e.getMessage());
            return "";
        }
    }

    @Override
    public String encryptObj(Object obj) {
        Objects.requireNonNull(obj);
        try {
            String json = objectMapper.writeValueAsString(obj);
            return this.encrypt(json);
        } catch (JsonProcessingException e) {
            log.warn("加密对象失败 reason: {}", e.getMessage());
        }
        return "";
    }

    @Override
    public String decrypt(String content) {
        try {
            return AesEncrypt.decrypt(content, secret);
        } catch (GeneralSecurityException e) {
            log.debug("解密失败 reason: {}", e.getMessage());
            return "";
        }
    }

    @Override
    public <T> T decrypt(String content, Class<T> cla) {
        try {
            String decrypt = AesEncrypt.decrypt(content, secret);
            return objectMapper.readValue(decrypt, cla);
        } catch (GeneralSecurityException | JsonProcessingException e) {
            log.debug("转换密文失败 reason: {}", e.getMessage());
        }
        return (T) null;
    }
}
