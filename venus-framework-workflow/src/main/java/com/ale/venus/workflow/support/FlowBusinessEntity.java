package com.ale.venus.workflow.support;

import java.lang.annotation.*;

/**
 * 流程业务实体
 *
 * @author Ale
 * @version 1.0.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FlowBusinessEntity {

    /**
     * 实体名称
     *
     * @return 实体名称
     */
    String name();

    /**
     * 业务类型
     *
     * @return 业务类型
     */
    String businessType();

    /**
     * 业务ID字段
     *
     * @return 业务ID字段
     */
    String businessIdField() default "id";

    /**
     * 流程实例ID字段
     *
     * @return 流程实例ID字段
     */
    String instanceIdField() default "flowInstanceId";

    /**
     * 流程实例状态字段
     *
     * @return 流程实例状态字段
     */
    String instanceStateField() default "flowInstanceState";

}
