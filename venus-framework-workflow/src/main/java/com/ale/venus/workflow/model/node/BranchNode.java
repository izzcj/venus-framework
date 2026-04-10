package com.ale.venus.workflow.model.node;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分支节点
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BranchNode extends LogicNode {

    /**
     * 分支列表
     */
    @JsonProperty(index = 88)
    protected List<FlowNode> branch;

    @Override
    protected List<FlowNode> flattenBranch() {
        List<FlowNode> nodes = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(this.branch)) {
            for (FlowNode child : this.branch) {
                nodes.addAll(child.flatten());
                child.child = null;
            }
            this.branch = null;
        }
        return nodes;
    }
}
