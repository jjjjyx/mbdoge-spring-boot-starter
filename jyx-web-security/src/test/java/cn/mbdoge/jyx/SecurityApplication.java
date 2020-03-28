package cn.mbdoge.jyx;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.User;
import cn.mbdoge.jyx.jwt.filter.BearerAuthenticationFilterAdapter;
import cn.mbdoge.jyx.security.ConfigureHttpSecurity;
import cn.mbdoge.jyx.security.EnableSecurityConfigure;
import cn.mbdoge.jyx.web.tomcat.WebServerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication()
public class SecurityApplication implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
//        WebMvcAutoConfiguration
//        System.out.println(Arrays.toString(args));
    }



    @Configuration
    public static class SecurityConfigure2 extends EnableSecurityConfigure {
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Bean("userDetailsServiceImpl")
        @Override
        public UserDetailsService userDetailsService() {
            return username -> {
                System.out.println("username = " + username);
                User vo = new User();

                vo.setUsername(username);
//                vo.setPassword(passwordEncoder.encode(username + "1"));
                vo.setPassword(passwordEncoder.encode(username));
                vo.setUserStatus(0);
                // 30 s 后过期
                Date date = new Date(System.currentTimeMillis() + 1000 * 30);
                vo.setNextExpireTime(date);
                List<GrantedAuthority> test = Arrays.asList(
                        new SimpleGrantedAuthority("TEST"),
                        new SimpleGrantedAuthority("XX")
                );
                vo.setAuthorities(test);

//                user.getRoles().stream()
//                        .map(item -> new SimpleGrantedAuthority(item.getName().toUpperCase())).collect(Collectors.toList())
//                vo.setAuthorities();
                return vo;
            };
        }

        @Override
        public ConfigureHttpSecurity configureHttpSecurity() {
            return (httpSecurity) -> {
                httpSecurity.authorizeRequests()
                        .antMatchers(HttpMethod.GET, "/a/**").permitAll()
                        .anyRequest().authenticated();
            };
        }

        @Bean
        @Override
        public BearerAuthenticationFilterAdapter bearerAuthenticationFilterAdapter(JwtTokenProvider jwtTokenProvider, AuthenticationEntryPoint authenticationEntryPoint) {
            return new BearerAuthenticationFilterAdapter(jwtTokenProvider, authenticationEntryPoint) {
            };
        }
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        return new WebServerFactory();
    }

    public static void main(String[] args) {

        try {
            SpringApplication.run(SecurityApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
