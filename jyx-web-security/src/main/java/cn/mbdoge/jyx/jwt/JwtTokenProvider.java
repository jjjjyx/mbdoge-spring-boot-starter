package cn.mbdoge.jyx.jwt;

import cn.mbdoge.jyx.security.SecurityProperties;
import io.jsonwebtoken.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final long expiration;
    private byte[] secret = {'s','e','c','r','e','t'};

    public JwtTokenProvider(JwtProperties jwtProperties) {
        if (!StringUtils.isEmpty(jwtProperties.getSecret())) {
            this.secret = Base64.getEncoder().encode(jwtProperties.getSecret().getBytes());
        }
        this.expiration = jwtProperties.getExpiration();
    }

    public String createToken(UserDetails userDetails, String id) {
        Date now = new Date();
        Claims claims = Jwts
                .claims()
                .setSubject(userDetails.getUsername());
        List<String> roles = userDetails
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
//
//        userKey = geOnlineListKey(userDetails);
//        redisTemplate.opsForSet().add(userKey, userDetails.getUid());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .setId(id)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Jws<Claims> getClaimsFromToken(String token) {
//        try {
        return Jwts.parser()
                .require(Constant.CHECK_KEY, Constant.CHECK_VALUE)
                .setSigningKey(secret)
                .parseClaimsJws(token);
//        } catch (ExpiredJwtException e) {
//            throw new InvalidJwtAuthenticationException("AccountStatusUserDetailsChecker.token.expired" , e);
//        } catch (Exception e) {
//            throw new InvalidJwtAuthenticationException("AbstractAccessDecisionManager.accessDenied", e);
//        }
    }

}
