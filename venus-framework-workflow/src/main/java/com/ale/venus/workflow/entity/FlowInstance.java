package com.ale.venus.workflow.entity;

import com.ale.venus.workflow.support.IdGeneratorSupport;
import com.ale.venus.workflow.support.JsonVariableAccessor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 流程实例
 * 指执行中的流程实例
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class FlowInstance extends FlowEntity implements JsonVariableAccessor {

    /**
     * 流程定义ID
     */
    protected String definitionId;

    /**
     * 流程定义设计内容
     */
    protected String designContent;

    /**
     * 业务类型
     */
    protected String businessType;

    /**
     * 业务ID
     */
    protected String businessId;

    /**
     * 当前所在节点Id
     */
    protected String currentNodeId;

    /**
     * 流程实例期望完成时间
     */
    protected LocalDateTime expireTime;

    /**
     * 标题
     */
    protected String title;

    /**
     * 描述
     */
    protected String description;

    /**
     * 变量
     */
    protected String variable;

    /**
     * 创建流程实例
     *
     * @param businessId   业务ID
     * @param businessType 业务类型
     * @param variables    变量
     * @param createdBy    创建人
     * @return 流程实例
     */
    public static FlowInstance of(String businessId, String businessType, Map<String, Object> variables, String createdBy) {
        FlowInstance flowInstance = new FlowInstance();
        flowInstance.setId(IdGeneratorSupport.generateId());
        flowInstance.setBusinessId(businessId);
        flowInstance.setBusinessType(businessType);
        flowInstance.addVariable(variables);
        flowInstance.setCreatedBy(createdBy);
        flowInstance.setCreatedAt(LocalDateTime.now());
        flowInstance.setUpdatedBy(createdBy);
        flowInstance.setUpdatedAt(LocalDateTime.now());
        return flowInstance;
    }
}
