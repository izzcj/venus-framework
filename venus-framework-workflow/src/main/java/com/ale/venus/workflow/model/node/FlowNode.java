package com.ale.venus.workflow.model.node;

import com.ale.venus.workflow.enumeration.NodeExecuteState;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * 流程节点
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = FlowNode.class, visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StartNode.class, name = StartNode.NODE_TYPE),
    @JsonSubTypes.Type(value = ConditionNode.class, name = ConditionNode.NODE_TYPE),
    @JsonSubTypes.Type(value = ExclusiveNode.class, name = ExclusiveNode.NODE_TYPE),
    @JsonSubTypes.Type(value = ParallelNode.class, name = ParallelNode.NODE_TYPE),
    @JsonSubTypes.Type(value = ApprovalNode.class, name = ApprovalNode.NODE_TYPE),
    @JsonSubTypes.Type(value = CarbonCopyNode.class, name = CarbonCopyNode.NODE_TYPE),
    @JsonSubTypes.Type(value = TriggerNode.class, name = TriggerNode.NODE_TYPE),
    @JsonSubTypes.Type(value = EndNode.class, name = EndNode.NODE_TYPE)
})
public abstract class FlowNode implements ExecutableNode, Serializable {

    /**
     * 节点id
     */
    @JsonProperty(index = 1)
    protected String id;

    /**
     * 父节点id
     */
    @JsonProperty(index = 2)
    protected String parentId;

    /**
     * 分支id
     */
    @JsonProperty(index = 3)
    protected String branchId;

    /**
     * 节点名称
     */
    @JsonProperty(index = 4)
    protected String name;

    /**
     * 节点类型
     */
    @JsonProperty(index = 5)
    protected String type;

    /**
     * 子节点
     */
    @JsonProperty(index = 99)
    protected FlowNode child;

    /**
     * 额外配置
     */
    @JsonProperty(index = 6)
    protected Map<String, Object> extraConfig;

    /**
     * 节点执行状态
     */
    @JsonProperty(index = 7)
    protected NodeExecuteState executeState = NodeExecuteState.NOT_EXECUTED;

    /**
     * 扁平化
     * 为避免扁平化的数据子节点嵌套过深，因此在处理后需将当前节点的子节点置空，分支同理
     *
     * @return 流程节点
     */
    public List<FlowNode> flatten() {
        List<FlowNode> nodes = Lists.newArrayList();
        Queue<FlowNode> queue = Lists.newLinkedList();
        queue.offer(this);
        while (!queue.isEmpty()) {
            FlowNode node = queue.poll();
            nodes.add(node);
            if (node.child != null) {
                queue.offer(node.child);
                nodes.addAll(node.child.flattenBranch());
                node.child = null;
            }
        }
        return nodes;
    }

    /**
     * 扁平化分支
     *
     * @return 分支节点
     */
    protected List<FlowNode> flattenBranch() {
        return Lists.newArrayList();
    }
}
