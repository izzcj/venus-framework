package com.ale.venus.security.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * 默认预认证检查器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class DefaultPreAuthenticationChecker implements UserDetailsChecker {

    @Override
    public void check(UserDetails toCheck) {
        if (!toCheck.isAccountNonLocked()) {
            throw new LockedException("账号已被锁定");
        }
        if (!toCheck.isEnabled()) {
            throw new DisabledException("账号已被禁用");
        }
        if (!toCheck.isAccountNonExpired()) {
            throw new AccountExpiredException("账号已过期");
        }
    }
}
