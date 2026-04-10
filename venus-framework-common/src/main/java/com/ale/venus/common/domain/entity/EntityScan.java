package com.ale.venus.common.domain.entity;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 实体扫描，不指定路径，默认扫描当前所在的包及子包
 *
 * @author Ale
 * @version 1.0.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({EntityInitializers.class, EntityScannerRegistrar.class})
public @interface EntityScan {

    /**
     * 扫描包路径
     *
     * @return 包路径
     */
    String[] value() default {};
}
