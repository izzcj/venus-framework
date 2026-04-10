package com.ale.venus.security.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import java.util.function.Supplier;

/**
 * Supplier延迟安全上下文实现
 * <p>
 * 因为SupplierDeferredSecurityContext作用域不支持包外访问，
 * 因此复制出来使用
 * </p>
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class SupplierDeferredSecurityContext implements DeferredSecurityContext {

    /**
     * 安全上下文提供器
     */
    private final Supplier<SecurityContext> supplier;

    /**
     * 安全上下文持有策略
     */
    private final SecurityContextHolderStrategy strategy;

    /**
     * 安全上下文
     */
    private SecurityContext securityContext;

    /**
     * 是否缺失安全上下文
     */
    private boolean missingContext;

    @Override
    public boolean isGenerated() {
        this.init();
        return this.missingContext;
    }

    @Override
    public SecurityContext get() {
        this.init();
        return this.securityContext;
    }

    /**
     * 初始化安全上下文
     */
    private void init() {
        if (this.securityContext != null) {
            return;
        }

        this.securityContext = this.supplier.get();
        this.missingContext = this.securityContext == null;
        if (this.missingContext) {
            this.securityContext = this.strategy.createEmptyContext();
        }
    }

}
