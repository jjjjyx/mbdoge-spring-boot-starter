package cn.mbdoge.jyx.jwt;

//import cn.mbdoge.jyx.jwt.core.JwtTokenFilterConfigure;
import cn.mbdoge.jyx.jwt.core.User;
import cn.mbdoge.jyx.jwt.util.TokenUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Base64;
//import org.springframework.boot.autoconfigure.AutoConfigureOrder;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.core.Ordered;

/**
 * 需要使用者提供一些操作类，
 * 比如，保存 token
 * 验证用户
 * redis
 */
@Slf4j
@Configuration(proxyBeanMethods =false)
@EnableConfigurationProperties({JwtProperties.class})
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 12)
//@Import({JwtTokenFilterConfigure.class})
//extends WebSecurityConfigurerAdapter
public class JwtConfigure  {

    private final JwtProperties jwtProperties;
//    private final PasswordEncoder passwordEncoder;
//    private final UserDetailsService userDetailsService;
//
//    // 需要 UserDetailsService
//    // PasswordEncoder
//    // 设计到api 加密的情况，在处理 AuthenticationEntryPoint 需要暴露
//

    public JwtConfigure(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }


    @Bean
    public TokenFactory tokenFactory () {
        final String secret = jwtProperties.getSecret();
        final byte[] encode = Base64.getEncoder().encode(secret.getBytes());
        long expiration = jwtProperties.getExpiration();
        return new TokenFactory() {
            @Override
            public String createToken(User user) {
                return TokenUtils.createToken(user, expiration, encode);
            }

            @Override
            public Jws<Claims> getTokenClaims(String token) {
                return TokenUtils.getClaimsFromToken(token, encode);
            }
        };
    }

}
