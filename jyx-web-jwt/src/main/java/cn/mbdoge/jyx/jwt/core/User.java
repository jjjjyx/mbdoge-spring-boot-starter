package cn.mbdoge.jyx.jwt.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jyx
 */
@Data
public class User implements UserDetails, Serializable {

    private static final long serialVersionUID = 982649350878695388L;
    @JsonView(DataView.AdminView.class)
    private Long id;
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Integer userStatus;
    private Date nextExpireTime;

    @JsonIgnore
    private List<GrantedAuthority> authorities;

    public List<String> getRoles () {
        return this.authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

    /**
     * 用户唯一标识
     */
    private String uid;
    /**
     * 登陆时间
     */
    private Date loginTime;
    /**
     * 登录IP地址
     */
    @JsonView(DataView.AdminView.class)
    private String ipAddr;
    /**
     * 登录地点
     */
    @JsonView(DataView.AdminView.class)
    private String loginLocation;
    /**
     * 浏览器类型
     */
    private String browser;
    /**
     * 操作系统
     */
    @JsonView(DataView.AdminView.class)
    private String os;


//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
//    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * @return 返回true 表示没有过期
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        // 账户是否过期

        if (this.nextExpireTime == null) {
            return false;
        }

        Date currentDate = new Date();
        Date expireDate = this.nextExpireTime;
        // 无限时间
        if (expireDate == null) {
            return true;
        }

        return currentDate.before(expireDate);
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.userStatus == 0;
    }
}
