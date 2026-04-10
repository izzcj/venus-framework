package com.ale.venus.workflow.parser;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.utils.JsonUtils;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.exception.FlowException;
import com.ale.venus.workflow.model.InstanceModel;
import com.ale.venus.workflow.model.node.FlowNode;


/**
 * 默认流程实例模型解析器
 *
 * @author Ale
 * @version 1.0.0
 */
public class DefaultInstanceModelParser implements InstanceModelParser {

    @Override
    public InstanceModel parse(FlowInstance flowInstance) {
        if (flowInstance == null) {
            throw new FlowException("解析流程实例模型失败！流程实例为空！");
        }
        if (StrUtil.isBlank(flowInstance.getDesignContent())) {
            throw new FlowException("解析流程实例模型失败！流程设计内容为空！");
        }
        FlowNode flowNode = JsonUtils.fromJson(flowInstance.getDesignContent(), FlowNode.class);
        return InstanceModel.builder()
            .id(flowInstance.getId())
            .businessId(flowInstance.getBusinessId())
            .businessType(flowInstance.getBusinessType())
            .rootNode(JsonUtils.fromJson(flowInstance.getDesignContent(), FlowNode.class))
            .flowInstance(flowInstance)
            .flowNodes(flowNode.flatten())
            .build();
    }
}
