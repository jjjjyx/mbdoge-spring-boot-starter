package cn.mbdoge.jyx.jwt.util;

import cn.mbdoge.jyx.jwt.core.Constant;
import cn.mbdoge.jyx.jwt.core.User;
import cn.mbdoge.jyx.jwt.exception.InvalidJwtAuthenticationException;
import io.jsonwebtoken.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class TokenUtils {
    private TokenUtils() {
    }


    public static String createToken(User user, long expiration, byte[] secret) {
        Date now = new Date();
        Claims claims = Jwts
                .claims()
                .setSubject(user.getUsername());
        List<String> roles = user
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        claims.put(Constant.CLAIM_KEY_ROLES, roles);
        claims.put(Constant.CLAIM_KEY_CREATED, now);
        claims.put(Constant.CHECK_KEY, Constant.CHECK_VALUE);

        Date validity = new Date(now.getTime() + expiration * 1000);

//        String userKey = getUserKey(userDetails);
//        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
//        operation.set(userKey, userDetails, expiration, TimeUnit.SECONDS);

//        userKey = geOnlineListKey(userDetails);
//        redisTemplate.opsForSet().add(userKey, userDetails.getUid());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .setId(user.getUid())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public static Jws<Claims> getClaimsFromToken(String token, byte[] secret) {
        return Jwts.parser()
                .require(Constant.CHECK_KEY, Constant.CHECK_VALUE)
                .setSigningKey(secret)
                .parseClaimsJws(token);
//        ExpiredJwtException
//        try {
//
//        } catch (ExpiredJwtException e) {
//            throw new InvalidJwtAuthenticationException("AccountStatusUserDetailsChecker.token.expired" , e);
//        } catch (Exception e) {
//            throw new InvalidJwtAuthenticationException("AbstractAccessDecisionManager.accessDenied", e);
//        }
    }

}
