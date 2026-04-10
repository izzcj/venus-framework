package com.ale.venus.core.service.hook;

import com.ale.venus.common.domain.entity.BaseEntity;
import com.ale.venus.common.porxy.ProxyResolvable;
import com.ale.venus.common.support.TriConsumer;
import com.ale.venus.common.utils.CastUtils;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;

/**
 * service钩子执行器支持
 *
 * @param <E>      实体类型
 * @param <H>      钩子执行器支持类型
 * @author Ale
 * @version 1.0.0
 */
public abstract class ServiceHookExecutorSupport<E extends BaseEntity, H extends ServiceHookExecutorSupport<E, H>> extends AbstractHookExecutorSupport implements LocalServiceHook<E>, ServiceHookExecutor, ServiceHookInvokerHolder<E>, ProxyResolvable<H> {

    /**
     * 全局service钩子
     */
    private ObjectProvider<GlobalServiceHook> globalServiceHooks;

    @Resource
    public void setGlobalServiceHooks(ObjectProvider<GlobalServiceHook> globalServiceHooks) {
        this.globalServiceHooks = globalServiceHooks;
    }

    @Override
    public void executeGlobalServiceHook(Object param, HookStage hookStage, HookContext hookContext) {
        if (hookContext == null) {
            hookContext = this.createHookContext();
        }
        for (GlobalServiceHook globalServiceHook : this.globalServiceHooks) {
            this.getGlobalHookInvoker(hookStage).accept(globalServiceHook, param, hookContext);
        }
    }

    @Override
    public void executeLocalServiceHook(Object param, HookStage hookStage, HookContext hookContext) {
        if (hookContext == null) {
            hookContext = this.createHookContext();
        }
        this.getLocalHookInvoker(hookStage).accept(this.resolveProxy(), param, hookContext);
    }

    @Override
    public void executeServiceHooks(Object param, HookStage hookStage, HookContext hookContext) {
        this.executeLocalServiceHook(param, hookStage, hookContext);
        this.executeGlobalServiceHook(param, hookStage, hookContext);
    }

    @Override
    public TriConsumer<GlobalServiceHook, Object, HookContext> getGlobalHookInvoker(HookStage hookStage) {
        return getServiceHookInvoker(hookStage);
    }

    @Override
    public TriConsumer<LocalServiceHook<E>, Object, HookContext> getLocalHookInvoker(HookStage hookStage) {
        return getServiceHookInvoker(hookStage);
    }

    /**
     * 获取service钩子回调器
     *
     * @param <T> 钩子类型
     * @param hookStage 钩子阶段
     * @return 钩子调用器
     */
    @Nonnull
    private <T extends ServiceHook<?>> TriConsumer<T, Object, HookContext> getServiceHookInvoker(HookStage hookStage) {
        return switch (hookStage) {
            case BEFORE_QUERY -> (hook, param, context) -> hook.beforeQuery(CastUtils.cast(param), context);
            case AFTER_QUERY -> (hook, param, context) -> hook.afterQuery(CastUtils.cast(param), context);
            case AFTER_QUERY_LIST -> (hook, param, context) -> hook.afterQueryList(CastUtils.cast(param), context);
            case BEFORE_CREATE -> (hook, param, context) -> hook.beforeCreate(CastUtils.cast(param), context);
            case AFTER_CREATE -> (hook, param, context) -> hook.afterCreate(CastUtils.cast(param), context);
            case BEFORE_BATCH_CREATE -> (hook, param, context) -> hook.beforeBatchCreate(CastUtils.cast(param), context);
            case AFTER_BATCH_CREATE -> (hook, param, context) -> hook.afterBatchCreate(CastUtils.cast(param), context);
            case BEFORE_MODIFY -> (hook, param, context) -> hook.beforeModify(CastUtils.cast(param), context);
            case AFTER_MODIFY -> (hook, param, context) -> hook.afterModify(CastUtils.cast(param), context);
            case BEFORE_BATCH_MODIFY -> (hook, param, context) -> hook.beforeBatchModify(CastUtils.cast(param), context);
            case AFTER_BATCH_MODIFY -> (hook, param, context) -> hook.afterBatchModify(CastUtils.cast(param), context);
            case BEFORE_SAVE -> (hook, param, context) -> hook.beforeSave(CastUtils.cast(param), context);
            case AFTER_SAVE -> (hook, param, context) -> hook.afterSave(CastUtils.cast(param), context);
            case BEFORE_BATCH_SAVE -> (hook, param, context) -> hook.beforeBatchSave(CastUtils.cast(param), context);
            case AFTER_BATCH_SAVE -> (hook, param, context) -> hook.afterBatchSave(CastUtils.cast(param), context);
            case BEFORE_DELETE -> (hook, param, context) -> hook.beforeDelete(CastUtils.cast(param), context);
            case AFTER_DELETE -> (hook, param, context) -> hook.afterDelete(CastUtils.cast(param), context);
            case BEFORE_BATCH_DELETE -> (hook, param, context) -> hook.beforeBatchDelete(CastUtils.cast(param), context);
            case AFTER_BATCH_DELETE -> (hook, param, context) -> hook.afterBatchDelete(CastUtils.cast(param), context);
            case BEFORE_CLEAR_HOOK_CONTEXT -> (hook, param, context) -> hook.beforeClearHookContext(context);
        };
    }
}
