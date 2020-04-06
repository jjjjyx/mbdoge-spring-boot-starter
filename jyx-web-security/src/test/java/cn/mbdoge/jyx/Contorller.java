package cn.mbdoge.jyx;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class Contorller {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public Contorller(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/a/input2")
    @ResponseBody
//    @PreAuthorize("isAuthenticated()")
//    @PreAuthorize("isAuthenticated()")
    public Map input2(@RequestParam(name = "test") String test){
        String s = "aaa";
        Map m = new HashMap();
        m.put("a", "a");
        m.put("s", s);
        m.put("d", test);
        return m;
    }

    @GetMapping(value = "/a/login")
    @ResponseBody
    public String login(@RequestParam(name = "test") String test){
        System.out.println("test = " + test);
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(test, test);
        final Authentication authentication = authenticationManager.authenticate(upToken);
        User principal = (User) authentication.getPrincipal();
        System.out.println("principal = " + principal);
        String id = UUID.randomUUID().toString();

            // 在登录的时候有
            // 过期， 禁用
            // 密码错误
//            AccountExpiredException User account has expired
//            DisabledException: User is disabled
//            BadCredentialsException: Bad credentials
        String token = null;
        try {
            token = jwtTokenProvider.createToken(principal, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
}
