package com.ale.venus.workflow.executor;

import com.ale.venus.workflow.parser.ConditionParser;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * 条件执行器支持初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
public class ConditionExecutorSupportInitializer implements SmartInitializingSingleton {

    /**
     * 条件解析器
     */
    private final ConditionParser conditionParser;

    /**
     * 条件执行器
     */
    private final ConditionExecutor conditionExecutor;

    public ConditionExecutorSupportInitializer(ConditionParser conditionParser, ConditionExecutor conditionExecutor) {
        this.conditionParser = conditionParser;
        this.conditionExecutor = conditionExecutor;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            MethodHandles.privateLookupIn(ConditionExecutor.class, MethodHandles.lookup())
                .findStatic(ConditionExecutorSupport.class, "setConditionExecutor", MethodType.methodType(void.class, ConditionExecutor.class))
                .invoke(this.conditionExecutor);
            MethodHandles.privateLookupIn(ConditionExecutorSupport.class, MethodHandles.lookup())
                .findStatic(ConditionExecutorSupport.class, "setConditionParser", MethodType.methodType(void.class, ConditionParser.class))
                .invoke(this.conditionParser);
        } catch (Throwable e) {
            throw new RuntimeException("初始化条件执行器支持类失败！");
        }
    }
}
