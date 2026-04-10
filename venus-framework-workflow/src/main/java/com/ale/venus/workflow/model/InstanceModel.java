package com.ale.venus.workflow.model;

import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.model.node.BranchNode;
import com.ale.venus.workflow.model.node.FlowNode;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * 流程实例模型
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
@Setter
@Builder
public class InstanceModel implements Serializable {

    /**
     * 流程实例ID
     */
    private String id;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 根节点
     */
    private FlowNode rootNode;

    /**
     * 流程实例
     */
    private FlowInstance flowInstance;

    /**
     * 流程节点集合
     * 去树化后的节点集合
     */
    List<FlowNode> flowNodes;

    /**
     * 查找节点
     *
     * @param nodeId 节点ID
     * @return 节点
     */
    public FlowNode findNode(String nodeId) {
        return this.findNode(this.rootNode, nodeId);
    }

    /**
     * 查找节点
     *
     * @param rootNode 根节点
     * @param nodeId   节点ID
     * @return 节点
     */
    public FlowNode findNode(FlowNode rootNode, String nodeId) {
        Queue<FlowNode> queue = Lists.newLinkedList();
        queue.offer(rootNode);
        while (!queue.isEmpty()) {
            FlowNode node = queue.poll();
            if (Objects.equals(node.getId(), nodeId)) {
                return node;
            }
            if (node instanceof BranchNode branchNode) {
                queue.addAll(branchNode.getBranch());
            }
            if (node.getChild() != null) {
                queue.offer(node.getChild());
            }
        }
        return null;
    }

    /**
     * 获取节点的子节点
     *
     * @param nodeId 节点ID
     * @return 子节点
     */
    public FlowNode findChild(String nodeId) {
        return this.findChild(this.rootNode, nodeId);
    }

    /**
     * 查找子级
     *
     * @param rootNode 根节点
     * @param nodeId   节点ID
     * @return 节点
     */
    public FlowNode findChild(FlowNode rootNode, String nodeId) {
        Queue<FlowNode> queue = Lists.newLinkedList();
        queue.offer(rootNode);
        while (!queue.isEmpty()) {
            FlowNode node = queue.poll();
            if (Objects.equals(node.getParentId(), nodeId) && !Objects.equals(node.getBranchId(), nodeId)) {
                return node;
            }
            if (node instanceof BranchNode branchNode) {
                queue.addAll(branchNode.getBranch());
            }
            if (node.getChild() != null) {
                queue.offer(node.getChild());
            }
        }
        return null;
    }

    /**
     * 查找节点的分支
     *
     * @param nodeId 节点ID
     * @return 分支
     */
    public List<FlowNode> findBranch(String nodeId) {
        return this.findBranch(this.rootNode, nodeId);
    }

    /**
     * 查找分支
     *
     * @param rootNode 根节点
     * @param nodeId   节点ID
     * @return 节点
     */
    public List<FlowNode> findBranch(FlowNode rootNode, String nodeId) {
        Queue<FlowNode> queue = Lists.newLinkedList();
        queue.offer(rootNode);
        while (!queue.isEmpty()) {
            FlowNode node = queue.poll();
            if (node instanceof BranchNode branchNode) {
                if (Objects.equals(node.getId(), nodeId)) {
                    return branchNode.getBranch();
                }
                queue.addAll(branchNode.getBranch());
            }
            if (node.getChild() != null) {
                queue.offer(node.getChild());
            }
        }
        return null;
    }
}
