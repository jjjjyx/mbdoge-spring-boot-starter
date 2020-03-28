package cn.mbdoge.jyx.jwt;

import cn.mbdoge.jyx.security.WebSecurityProperties;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {


    private final WebSecurityProperties.Jwt jwt;

    private final byte[] secret;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageSourceAccessor message;


    public JwtTokenProvider(WebSecurityProperties webSecurityProperties, RedisTemplate<String, Object> redisTemplate, @Qualifier("webMessageSourceAccessor") MessageSourceAccessor messageSourceAccessor) {
        this.jwt = webSecurityProperties.getJwt();
        this.redisTemplate = redisTemplate;
        this.message = messageSourceAccessor;
        this.secret = Base64.getEncoder().encode(jwt.getSecret().getBytes());
    }

    public String createToken(UserDetails userDetails, String id) {
        Date now = new Date();
        String username = userDetails.getUsername();

        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        Claims claims = Jwts.claims();

        claims.setSubject(username);
        claims.put(Constant.CLAIM_KEY_ROLES, roles);
        claims.put(Constant.CLAIM_KEY_CREATED, now);
        claims.put(Constant.CHECK_KEY, Constant.CHECK_VALUE);

        Date validity = new Date(now.getTime() + jwt.getExpiration() * 1000);
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();

        String key = getUserKey(username, id);
        operation.set(key, userDetails, jwt.getExpiration(), TimeUnit.SECONDS);


        String compact = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .setId(id)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        log.trace("user = {} gen token success! token size = {} id = {}", username, compact.length(), id);
        return compact;
    }

    public UserDetails getUserDetails(String token) {

        Jws<Claims> claims = getClaimsFromToken(token);

        Claims body = claims.getBody();
        String id = body.getId();
        String username = body.getSubject();


        String key = getUserKey(username, id);

        if (!redisTemplate.hasKey(key)) {
            throw new BadCredentialsException(message.getMessage("BindAuthenticator.badCredentials", "token 无效"));
        }

        User userDetails = (User) redisTemplate.opsForValue().get(key);
        if (userDetails == null) {
            throw new BadCredentialsException(message.getMessage("BindAuthenticator.badCredentials", "token 过期"));
        }

        return userDetails;
    }

    public Jws<Claims> getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                .require(Constant.CHECK_KEY, Constant.CHECK_VALUE)
                .setSigningKey(secret)
                .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {

//            System.out.println(e.getMessage());
//            System.out.println("e.getClaims() = " + e.getClaims());
            throw new CredentialsExpiredException(message.getMessage("AccountStatusUserDetailsChecker.credentialsExpired", "token 过期") , e);
        } catch (Exception e) {
            // 其他情况的解析失败
            throw new BadCredentialsException(message.getMessage("BindAuthenticator.badCredentials", "token 无效"), e);
        }
    }

    public String getUserKey (String username, String id) {
        return this.jwt.getRedisKeyPrefix() + username + ":" +id;
    }

}
