package com.ale.venus.common.transaction;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Supplier;

/**
 * 事务支持
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionSupport {

    /**
     * 事务管理器
     */
    private static PlatformTransactionManager transactionManager;

    static void setTransactionManager(PlatformTransactionManager transactionManager) {
        TransactionSupport.transactionManager = transactionManager;
    }

    /**
     * 执行事务
     * 在执行逻辑中不要使用多线程，会导致事务失效
     *
     * @param <T> 返回值类型
     * @param execution 执行事务的逻辑
     * @return 执行是否成功
     */
    public static <T> T execute(Supplier<T> execution) {
        return execute(execution, new DefaultTransactionDefinition());
    }

    /**
     * 执行事务
     * 在执行逻辑中不要使用多线程，会导致事务失效
     *
     * @param <T>                   返回值类型
     * @param execution             执行事务的逻辑
     * @param transactionDefinition 事务定义
     * @return 执行是否成功
     */
    public static <T> T execute(Supplier<T> execution, TransactionDefinition transactionDefinition) {
        TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
        try {
            T result = execution.get();
            transactionManager.commit(status);
            return result;
        } catch (Exception e) {
            transactionManager.rollback(status);
            log.warn("事务异常回滚");
            throw e;
        }
    }

}
