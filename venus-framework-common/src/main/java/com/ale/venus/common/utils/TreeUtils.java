package com.ale.venus.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.common.support.TreeNode;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树工具
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TreeUtils {

    /**
     * 默认树顶级ID
     */
    public static final long DEFAULT_TOP_LEVEL_ID = 0L;

    /**
     * 树构建器
     *
     * @param <T> 树节点类型
     */
    @NoArgsConstructor
    public static class TreeBuilder<T extends TreeNode<T>> {

        /**
         * 树节点集合
         */
        private Collection<T> nodes;

        /**
         * 顶级ID集合
         */
        private Set<Long> topLevelIds;

        /**
         * 过滤器
         */
        private Function<T, Boolean> filter;

        /**
         * 设置树的节点
         *
         * @param nodes 节点列表
         * @return 树构建器
         */
        public TreeBuilder<T> nodes(Collection<T> nodes) {
            this.nodes = nodes;
            return this;
        }

        /**
         * 设置树的顶层ID
         *
         * @param topLevelId 顶层树节点ID
         * @return 树构建器
         */
        public TreeBuilder<T> topLevelId(Long topLevelId) {
            this.topLevelIds = Collections.singleton(topLevelId);
            return this;
        }

        /**
         * 设置树的顶层ID集合
         *
         * @param topLevelIds 顶层树节点ID集合
         * @return 树构建器
         */
        public TreeBuilder<T> topLevelIds(Set<Long> topLevelIds) {
            this.topLevelIds = topLevelIds;
            return this;
        }

        /**
         * 设置树节点的过滤器
         *
         * @param filter 过滤器
         * @return 树构建器
         */
        public TreeBuilder<T> filter(Function<T, Boolean> filter) {
            this.filter = filter;
            return this;
        }

        /**
         * 构建树
         *
         * @return 树顶级节点列表
         */
        public List<T> build() {
            if (CollectionUtil.isEmpty(this.topLevelIds) || CollectionUtil.isEmpty(this.nodes)) {
                return Collections.emptyList();
            }

            if (this.filter == null) {
                return this.nodes.stream()
                    .filter(node -> this.topLevelIds.contains(node.getParentId()))
                    .peek(this::buildSubNode)
                    .toList();
            }

            return this.nodes.stream()
                .filter(node -> this.topLevelIds.contains(node.getParentId()))
                .filter(node -> {
                    this.buildSubNode(node);
                    return CollectionUtil.isNotEmpty(node.getChildren()) || this.filter.apply(node);
                })
                .toList();
        }

        /**
         * 构建子节点
         *
         * @param node 树节点对象
         */
        private void buildSubNode(T node) {
            List<T> children = this.nodes.stream()
                .filter(n -> Objects.equals(n.getParentId(), node.getId()))
                .filter(n -> {
                    this.buildSubNode(n);
                    return this.filter == null || CollectionUtil.isNotEmpty(n.getChildren()) || this.filter.apply(n);
                })
                .toList();
            node.setChildren(CollectionUtil.isEmpty(children) ? null : children);
        }

        /**
         * 构建单节点树
         *
         * @return 树顶级节点
         */
        public T buildSingle() {
            List<T> treeNodes = this.build();
            if (CollectionUtil.isEmpty(treeNodes)) {
                return null;
            }

            return treeNodes.get(0);
        }
    }

    /**
     * 创建一个树
     *
     * @param <T>   树节点类型
     * @param nodes 节点
     * @return 树构建器
     */
    public static <T extends TreeNode<T>> TreeBuilder<T> newTreeBuilder(Collection<T> nodes) {
        return new TreeBuilder<T>().topLevelId(DEFAULT_TOP_LEVEL_ID)
            .nodes(nodes);
    }

    /**
     * 构建多顶级节点的树
     *
     * @param <T>   树节点类型
     * @param nodes 树节点集合
     * @return 树顶级节点
     */
    public static <T extends TreeNode<T>> List<T> buildTree(Collection<T> nodes) {
        return new TreeBuilder<T>()
            .topLevelId(DEFAULT_TOP_LEVEL_ID)
            .nodes(nodes)
            .build();
    }

    /**
     * 构建顶级只有一个节点的树
     *
     * @param <T>   树节点类型
     * @param nodes 树节点集合
     * @return 树顶级节点
     */
    public static <T extends TreeNode<T>> T buildSingleTree(Collection<T> nodes) {
        return new TreeBuilder<T>()
            .topLevelId(DEFAULT_TOP_LEVEL_ID)
            .nodes(nodes)
            .buildSingle();
    }

    /**
     * 构建树中所有叶子节点集合
     *
     * @param <T>   树节点类型
     * @param nodes 树节点集合
     * @return 叶子节点集合
     */
    public static <T extends TreeNode<T>> List<T> buildLeafNodes(Collection<T> nodes) {
        Set<Long> parentIds = nodes.stream()
            .map(TreeNode::getParentId)
            .collect(Collectors.toSet());

        return nodes.stream()
            .filter(node -> !parentIds.contains(node.getId()))
            .toList();
    }

    /**
     * 获取树的叶子节点
     *
     * @param <T>       树节点类型
     * @param treeNodes 树节点集合
     * @return 叶子节点集合
     */
    public static <T extends TreeNode<T>> List<T> getLeafNodes(Collection<T> treeNodes) {
        List<T> leafNodes = Lists.newArrayList();
        for (T node : treeNodes) {
            if (CollectionUtil.isNotEmpty(node.getChildren())) {
                leafNodes.addAll(getLeafNodes(node.getChildren()));
            } else {
                leafNodes.add(node);
            }
        }

        return leafNodes;
    }

}
