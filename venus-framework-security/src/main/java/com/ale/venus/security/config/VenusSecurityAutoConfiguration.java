package com.ale.venus.security.config;

import com.ale.venus.common.redis.RedisCommonAutoConfiguration;
import com.ale.venus.common.security.TokenManager;
import com.ale.venus.security.authentication.LoginProcessor;
import com.ale.venus.security.config.servlet.LoginProcessorsHolder;
import com.ale.venus.security.context.RedisSessionContext;
import com.ale.venus.security.context.SessionContext;
import com.ale.venus.security.context.SessionTouchedHandler;
import com.ale.venus.security.support.AuthenticatedUserConverter;
import com.ale.venus.security.support.DefaultAuthenticatedUserConverter;
import com.ale.venus.security.support.PropertiesAuthenticatedMvcPatternProvider;
import com.ale.venus.security.support.PropertiesPermittedMvcPatternProvider;
import com.ale.venus.security.token.jwt.JwtTokenManager;
import com.ale.venus.security.token.redis.RedisTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Venus安全框架自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@RequiredArgsConstructor
@AutoConfigureAfter(RedisCommonAutoConfiguration.class)
@EnableConfigurationProperties(VenusSecurityProperties.class)
public class VenusSecurityAutoConfiguration {

    /**
     * 安全配置
     */
    private final VenusSecurityProperties securityProperties;

    /**
     * Redis Token管理器Bean
     *
     * @param taskExecutor   任务执行器
     * @param sessionContext 会话上下文
     * @return Token管理器Bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "venus.security", name = "token-type", havingValue = "redis")
    public TokenManager redisTokenManager(@Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor, SessionContext sessionContext) {
        return new RedisTokenManager(this.securityProperties.getTokenExpiration(), taskExecutor, sessionContext);
    }

    /**
     * 设置默认用户信息转换器
     *
     * @return 转换器Bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "venus.security", name = "token-type", havingValue = "jwt", matchIfMissing = true)
    public AuthenticatedUserConverter venusUserConverter() {
        return new DefaultAuthenticatedUserConverter();
    }

    /**
     * JWT Token管理器Bean
     *
     * @param authenticatedUserConverter 认证用户转换器
     * @param sessionContext             会话上下文
     * @return Token管理器Bean
     */
    @Bean
    @ConditionalOnBean(AuthenticatedUserConverter.class)
    @ConditionalOnProperty(prefix = "venus.security", name = "token-type", havingValue = "jwt", matchIfMissing = true)
    public TokenManager jwtTokenManager(AuthenticatedUserConverter authenticatedUserConverter, SessionContext sessionContext) {
        return new JwtTokenManager(this.securityProperties.getTokenExpiration(), authenticatedUserConverter, sessionContext);
    }

    /**
     * 会话上下文Bean
     *
     * @param redisTemplate          RedisTemplate
     * @param taskExecutor           任务执行器
     * @param sessionTouchedHandlers 会话刷新后的处理器
     * @return 会话上下文Bean
     */
    @Bean
    public SessionContext sessionContext(RedisTemplate<String, Object> redisTemplate, 
                                         @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor,
                                         ObjectProvider<SessionTouchedHandler> sessionTouchedHandlers) {
        return new RedisSessionContext(redisTemplate, taskExecutor, sessionTouchedHandlers);
    }

    /**
     * 登录处理器持有器
     *
     * @param loginProcessors 登录处理器提供器
     * @return 登录处理器持有者Bean
     */
    @Bean
    public LoginProcessorsHolder loginProcessorsHolder(ObjectProvider<LoginProcessor> loginProcessors) {
        return new LoginProcessorsHolder(loginProcessors);
    }

    /**
     * 仅需认证的路径模式提供器Bean
     *
     * @return 提供器Bean
     */
    @Bean
    public PropertiesAuthenticatedMvcPatternProvider propertiesAuthenticatedMvcPatternProvider() {
        return new PropertiesAuthenticatedMvcPatternProvider(this.securityProperties);
    }

    /**
     * 无需认证的路径模式提供器
     *
     * @return 提供器Bean
     */
    @Bean
    public PropertiesPermittedMvcPatternProvider propertiesPermittedMvcPatternProvider() {
        return new PropertiesPermittedMvcPatternProvider(this.securityProperties);
    }
}
