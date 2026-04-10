package com.ale.venus.common.support;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;

/**
 * 补丁数据
 *
 * @param <T> 补丁数据类型
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor(staticName = "of")
public class PatchData<T> {

    /**
     * 新增的数据
     */
    private final Collection<T> added;

    /**
     * 修改的数据
     */
    private final Collection<T> changed;

    /**
     * 删除的数据
     */
    private final Collection<T> removed;

    public Collection<T> getAdded() {
        return this.added != null ? this.added : Collections.emptyList();
    }

    public Collection<T> getChanged() {
        return this.changed != null ? this.changed : Collections.emptyList();
    }

    public Collection<T> getRemoved() {
        return this.removed != null ? this.removed : Collections.emptyList();
    }

}
