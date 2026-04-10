package com.ale.venus.security.authentication;

import com.ale.venus.common.security.AuthenticatedUser;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证用户默认实现
 *
 * @author Ale
 * @version 1.0.0
 */
@Setter
public class DefaultAuthenticatedUser implements AuthenticatedUser {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 权限信息
     */
    private Set<? extends GrantedAuthority> authorities;

    @Override
    public String getAccount() {
        return this.account;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * 设置权限信息
     *
     * @param authorities 权限集合
     */
    public void setAuthorities(Collection<String> authorities) {
        this.authorities = authorities.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * 擦除密码
     */
    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
