package com.ale.venus.common.enumeration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 枚举扫描注解，不指定路径，默认扫描当前所在的包及子包
 *
 * @author Ale
 * @version 1.0.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({EnumInitializers.class, EnumScannerRegistrar.class})
public @interface EnumScan {

    /**
     * 扫描包路径
     *
     * @return 包路径
     */
    String[] value() default {};
}
