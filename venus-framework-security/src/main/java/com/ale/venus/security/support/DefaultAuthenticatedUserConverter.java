package com.ale.venus.security.support;

import com.ale.venus.common.security.AuthenticatedUser;
import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.security.authentication.DefaultAuthenticatedUser;
import com.google.common.collect.Maps;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认认证用户转换器
 *
 * @author Ale
 * @version 1.0.0
 */
public class DefaultAuthenticatedUserConverter implements AuthenticatedUserConverter {

    @Override
    public AuthenticatedUser convertToUser(Map<String, Object> userInfo) {
        DefaultAuthenticatedUser authenticatedUser = new DefaultAuthenticatedUser();
        authenticatedUser.setId((Long) userInfo.get("id"));
        authenticatedUser.setName((String) userInfo.get("name"));
        authenticatedUser.setAccount((String) userInfo.get("account"));
        Object authorityCollection = userInfo.get("authorities");
        if (authorityCollection instanceof Collection) {
            authenticatedUser.setAuthorities(CastUtils.cast(authorityCollection));
        }

        return authenticatedUser;
    }

    @Override
    public Map<String, Object> convertToMap(AuthenticatedUser authenticatedUser) {
        Map<String, Object> userInfo = Maps.newHashMap();
        userInfo.put("id", authenticatedUser.getId());
        userInfo.put("name", authenticatedUser.getName());
        userInfo.put("account", authenticatedUser.getAccount());
        if (authenticatedUser.getAuthorities() != null) {
            userInfo.put(
                "authorities",
                authenticatedUser.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet())
            );
        }

        return userInfo;
    }

}
