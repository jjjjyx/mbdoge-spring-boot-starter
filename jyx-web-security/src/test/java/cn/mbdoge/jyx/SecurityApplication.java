package cn.mbdoge.jyx;

import cn.mbdoge.jyx.jwt.JwtTokenProvider;
import cn.mbdoge.jyx.jwt.filter.BearerAuthenticationFilterAdapter;
import cn.mbdoge.jyx.security.EnableSecurityConfigure;
import cn.mbdoge.jyx.web.api.WebApiAutoConfigure;
import cn.mbdoge.jyx.web.tomcat.WebServerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication()
@Controller
@Configuration
@EnableAutoConfiguration
public class SecurityApplication implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println(Arrays.toString(args));
    }

    @Configuration
    public static class SecurityConfigure extends EnableSecurityConfigure {

        @Bean("userDetailsServiceImpl")
        @Override
        public UserDetailsService userDetailsService() {
            return username -> {
                System.out.println("username = " + username);
                return null;
            };
        }

        @Bean
        public BearerAuthenticationFilterAdapter customBearerAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
            return new BearerAuthenticationFilterAdapter(jwtTokenProvider) {

            };
        }
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        return new WebServerFactory();
    }

    public static void main(String[] args) {
        System.out.println("1111 = " + 1111);

        try {
            SpringApplication.run(SecurityApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/input2")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public Map input2(@RequestParam(name = "test") String test){
        String s = "aaa";
        Map m = new HashMap();
        m.put("a", "a");
        m.put("s", s);
        m.put("d", test);
        return m;
    }
}
