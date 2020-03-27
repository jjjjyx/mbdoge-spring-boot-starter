package cn.mbdoge.jyx.jwt;


import cn.mbdoge.jyx.jwt.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class SecurityUtils {

    public static String getUsername() {
        Object auto = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (auto instanceof UserDetails) {
            return ((UserDetails) auto).getUsername();
        } else if (auto instanceof String) {
            return (String) auto;
        }
        return auto.toString();
    }

    public static Collection<? extends GrantedAuthority> getUsernameAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public static List<String> getCurrentUserRoleList () {
        return getUsernameAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public static User getUser() {
        Object auto = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (auto instanceof User) {
            return (User) auto;
        }
        return null;
    }
}
