package com.ale.venus.core.service.hook;

/**
 * 抽象钩子执行器支持
 *
 * @author Ale
 * @version 1.0.0
 */
public abstract class AbstractHookExecutorSupport implements HookExecutor {

    @Override
    public HookContext createHookContext() {
        return HookContext.newContext();
    }

}
