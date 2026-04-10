package com.ale.venus.core.service.hook;

import java.io.Serializable;

/**
 * 钩子执行器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface HookExecutor extends Serializable {

    /**
     * 创建一个新的Hook上下文
     *
     * @return Hook上下文
     */
    HookContext createHookContext();

}
