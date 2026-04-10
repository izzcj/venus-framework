package com.ale.venus.common.support;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.common.utils.CastUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 树节点
 *
 * @param <T> 对象类型
 * @author Ale
 * @version 1.0.0
 */
public interface TreeNode<T> extends Serializable {

    /**
     * 获取ID
     *
     * @return ID
     */
    Long getId();

    /**
     * 获取父级ID
     *
     * @return ID
     */
    Long getParentId();

    /**
     * 设置子节点
     *
     * @param children 孩子节点
     */
    void setChildren(Collection<T> children);

    /**
     * 获取子节点
     *
     * @return 子节点
     */
    Collection<T> getChildren();

    /**
     * 获取最大深度
     *
     * @return 最大深度
     */
    default int getMaxDepth() {
        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.offer(this);
        int depth = 0;

        while (!queue.isEmpty()) {
            for (int i = 0, size = queue.size(); i < size; i++) {
                TreeNode<T> node = queue.poll();
                if (node == null) {
                    continue;
                }
                Collection<T> children = node.getChildren();
                if (CollectionUtil.isNotEmpty(children)) {
                    for (T child : children) {
                        queue.offer(CastUtils.cast(child));
                    }
                }
            }

            depth++;
        }

        return depth;
    }
}
