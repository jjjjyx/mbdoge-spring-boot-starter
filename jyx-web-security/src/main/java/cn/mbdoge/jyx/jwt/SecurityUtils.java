package cn.mbdoge.jyx.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jyx
 */
public final class SecurityUtils {
    private SecurityUtils() {
    }

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

    public static boolean hasRole(String role){
        return hasRole(getUser(), role);
    }

    public static boolean hasRole(UserDetails user,String role){
        if (user == null || role == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            if (role.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasAnyRoles(User user, String... roles) {
        for (String role : roles) {
            boolean b = hasRole(user, role);
            if (b) {
                return true;
            }
        }
        return false;
    }
}
