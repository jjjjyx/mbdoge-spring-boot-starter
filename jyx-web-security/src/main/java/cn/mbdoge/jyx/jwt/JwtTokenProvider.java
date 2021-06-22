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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author jyx
 */
@Slf4j
public class JwtTokenProvider {
    private final WebSecurityProperties.Jwt jwt;

    private final byte[] secret;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageSourceAccessor message;


    public JwtTokenProvider(WebSecurityProperties webSecurityProperties, RedisTemplate<String, Object> redisTemplate, MessageSourceAccessor messageSourceAccessor) {
        this.jwt = webSecurityProperties.getJwt();
        this.redisTemplate = redisTemplate;
        this.message = messageSourceAccessor;
        this.secret = Base64.getEncoder().encode(jwt.getSecret().getBytes());
    }
    public Set<String> getUserOnlineList(UserDetails userDetails) {
        return getUserOnlineList(userDetails.getUsername());
    }

    public Set<String> getUserOnlineList(String username) {
        String key = getUserKey(username, "*");
        return redisTemplate.keys(key);
    }



    private void checkUserJitNumber (UserDetails userDetails) {
        log.trace("checkUserJitNumber jwt.getJitMax() = {} ", jwt.getJitMax());
        Set<String> userOnlineList = this.getUserOnlineList(userDetails);
        if (jwt.getJitMax() == 0) {
            return;
        }
        log.trace("checkUserJitNumber userOnline size = {} ", userOnlineList.size());
        int l = userOnlineList.size() - jwt.getJitMax();
        if (l >= 0) {
            Iterator<String> iterator = userOnlineList.iterator();
            for (int i = 0; i <= l; i++) {
                String key = iterator.next();
                log.trace("用户数量超出最大值，删除 {}", key);
                redisTemplate.delete(key);
            }
        }
    }

    public String createToken(UserDetails userDetails, String id, long expiration) {
        this.checkUserJitNumber(userDetails);

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

        Date validity = new Date(now.getTime() + expiration * 1000);
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();

        String key = getUserKey(username, id);
        operation.set(key, userDetails, expiration, TimeUnit.SECONDS);


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

    public String createToken(UserDetails userDetails, String id) {
        return createToken(userDetails, id, jwt.getExpiration());
    }

    public void clearUser (UserDetails userDetails, String id) {
        String username = userDetails.getUsername();
        String key = getUserKey(username, id);
        redisTemplate.delete(key);
//        SecurityContextHolder.clearContext();
    }

    public void clearUser (String username) {
        for (String key : getUserOnlineList(username)) {
            log.trace("clearUser [{}] = {}", username, key);
            redisTemplate.delete(key);
        }
    }

    public void refreshUser (UserDetails userDetails, String id) {
        String username = userDetails.getUsername();
        String key = getUserKey(username, id);

        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        operation.set(key, userDetails);
//        SecurityContextHolder.clearContext();
    }


    public UserDetails getUserDetails(String token) {

        Jws<Claims> claims = getClaimsFromToken(token);

        Claims body = claims.getBody();
        String id = body.getId();
        String username = body.getSubject();


        String key = getUserKey(username, id);

        if (!redisTemplate.hasKey(key)) {
            log.debug("用户提交token 解析成功，但是在redis 中不存在，判定失效");
            throw new CredentialsExpiredException(message.getMessage("AccountStatusUserDetailsChecker.credentialsExpired", "凭证已过期"));
        }

        User userDetails = (User) redisTemplate.opsForValue().get(key);
        if (userDetails == null) {
            log.debug("用户提交token 解析成功，但是在redis 中不存在，判定失效");
            throw new CredentialsExpiredException(message.getMessage("AccountStatusUserDetailsChecker.credentialsExpired", "凭证已过期"));
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
            throw new CredentialsExpiredException(message.getMessage("AccountStatusUserDetailsChecker.credentialsExpired", "凭证已过期") , e);
        } catch (Exception e) {
            // 其他情况的解析失败
            log.debug("解析token失败，原因 = {} token = {}",e.getMessage(), token);
            throw new BadCredentialsException(message.getMessage("AbstractAccessDecisionManager.accessDenied", "凭证无效"), e);
        }
    }

    public String getUserKey (String username, String id) {
        return this.jwt.getRedisKeyPrefix() + username + ":" +id;
    }

}
