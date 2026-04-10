package com.ale.venus.core.service.hook;

import com.ale.venus.common.data.DataRepository;
import com.ale.venus.common.utils.CastUtils;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 钩子上下文
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
@NoArgsConstructor(staticName = "newContext")
public class HookContext implements DataRepository {

    /**
     * 数据存储Map
     */
    private final Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(32);

    /**
     * 是否终止执行
     */
    @Setter
    private boolean termination;

    @Override
    public <T> T set(String key, T value) {
        return CastUtils.cast(
            this.dataMap.put(key, value)
        );
    }

    @Override
    public boolean has(String key) {
        return this.dataMap.containsKey(key);
    }

    @Override
    public <T> T get(String key) {
        return CastUtils.cast(
            this.dataMap.get(key)
        );
    }

    @Override
    public <T> T getOrDefault(String key, T defaultValue) {
        return CastUtils.cast(
            this.dataMap.getOrDefault(key, defaultValue)
        );
    }

    @Override
    public <T> T getIfAbsent(String key, Supplier<T> supplier) {
        T value = this.get(key);
        if (value == null) {
            return supplier.get();
        }

        return value;
    }

    @Override
    public <T> T computeIfAbsent(String key, Function<String, T> mappingFunc) {
        return CastUtils.cast(
            this.dataMap.computeIfAbsent(key, mappingFunc)
        );
    }

    @Override
    public <T> T remove(String key) {
        return CastUtils.cast(
            this.dataMap.remove(key)
        );
    }

    @Override
    public void clear() {
        this.dataMap.clear();
    }
}
