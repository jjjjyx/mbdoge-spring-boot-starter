package cn.mbdoge.jyx.jwt;

import cn.mbdoge.jyx.jwt.core.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface TokenFactory {
    String createToken(User user);
    Jws<Claims> getTokenClaims(String token);
}
