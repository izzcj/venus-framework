package com.ale.venus.common.transaction;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Venus事务自动配置类
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
public class VenusTransactionAutoConfiguration {

    /**
     * 事务支持初始化器
     *
     * @param platformTransactionManager 事务管理器
     * @return 事务支持类初始化器bean
     */
    @Bean
    public TransactionSupportInitializer transactionSupportInitializer(PlatformTransactionManager platformTransactionManager) {
        return new TransactionSupportInitializer(platformTransactionManager);
    }

}
