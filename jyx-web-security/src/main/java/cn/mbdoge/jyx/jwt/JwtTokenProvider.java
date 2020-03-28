package cn.mbdoge.jyx.jwt;

import cn.mbdoge.jyx.jwt.exception.InvalidJwtAuthenticationException;
import cn.mbdoge.jyx.security.SecurityProperties;
import io.jsonwebtoken.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
public class JwtTokenProvider {


    private final SecurityProperties.Jwt jwt;

    private final byte[] secret;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtTokenProvider(RedisTemplate<String, Object> redisTemplate, SecurityProperties securityProperties) {
        this.jwt = securityProperties.getJwt();
        this.redisTemplate = redisTemplate;
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

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .setId(id)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public UserDetails getUserDetails(String token) {

        Jws<Claims> claims = getClaimsFromToken(token);

        Claims body = claims.getBody();
        String id = body.getId();
        String username = body.getSubject();


        String key = getUserKey(username, id);

        if (!redisTemplate.hasKey(key)) {
            throw new CredentialsExpiredException("token 失效");
        }

        User userDetails = (User) redisTemplate.opsForValue().get(key);
        if (userDetails == null) {
            throw new CredentialsExpiredException("token 失效");
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
            throw new InvalidJwtAuthenticationException("AccountStatusUserDetailsChecker.token.expired" , e);
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("AbstractAccessDecisionManager.accessDenied", e);
        }
    }

    public String getUserKey (String username, String id) {
        return this.jwt.getRedisKeyPrefix() + username + ":" +id;
    }

}
