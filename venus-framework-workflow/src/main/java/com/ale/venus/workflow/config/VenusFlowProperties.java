package com.ale.venus.workflow.config;

import com.ale.venus.common.support.EnableAwareProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Venus流程配置
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "venus.workflow")
public class VenusFlowProperties extends EnableAwareProperties {

    /**
     * 是否生成逻辑节点任务
     *
     * @see com.ale.venus.workflow.model.node.LogicNode
     */
    private boolean generateLogicTask;

    /**
     * 是否生成数据库表
     */
    private boolean generateDatabaseTable;

}
