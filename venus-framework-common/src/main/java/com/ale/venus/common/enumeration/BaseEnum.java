package com.ale.venus.common.enumeration;

import com.ale.venus.common.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 枚举接口
 *
 * @param <T> 枚举值泛型
 * @author Ale
 * @version 1.0.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public interface BaseEnum<T extends Serializable> {

    /**
     * 初始化
     */
    default void init() {
        EnumContainer.putEnum(this);
    }

    /**
     * 初始化
     *
     * @param value 枚举值
     */
    default void init(T value) {
        EnumContainer.putEnum(this, value, this.toString());
    }

    /**
     * 初始化
     *
     * @param value 枚举值
     * @param msg   描述
     */
    default void init(T value, String msg) {
        EnumContainer.putEnum(this, value, msg);
    }

    /**
     * 获取枚举值
     *
     * @return 枚举值
     */
    default T getValue() {
        return EnumContainer.getEnum(this).getValue();
    }

    /**
     * 获取枚举信息
     *
     * @return 枚举信息
     */
    default String getMsg() {
        return EnumContainer.getEnum(this).getMsg();
    }

    /**
     * 获取枚举名称
     *
     * @return 枚举名称
     */
    default String getName() {
        return EnumContainer.getEnum(this).getName();
    }

    /**
     * 通过枚举类型和枚举名称获取枚举
     *
     * @param <R>   枚举泛型
     * @param clazz 枚举类型
     * @param name  枚举名称
     * @return 枚举
     */
    static <R extends BaseEnum> R getByName(Class<R> clazz, String name) {
        return Stream.of(clazz.getEnumConstants())
            .filter(baseEnum -> baseEnum.getName().equals(name))
            .findAny()
            .orElseThrow(() -> new ServiceException("枚举值不存在！"));
    }

    /**
     * 通过枚举类型和枚举值获取枚举
     *
     * @param <R>   枚举泛型
     * @param clazz 枚举类型
     * @param value 枚举值
     * @return 枚举
     */
    static <R extends BaseEnum> R getByValue(Class<R> clazz, Object value) {
        return getByValue(clazz, value, "枚举值不存在！");
    }

    /**
     * 通过枚举类型和枚举值获取枚举
     *
     * @param <R>           枚举泛型
     * @param clazz         枚举类型
     * @param value         枚举值
     * @param errorMessage  枚举值
     * @return 枚举
     */
    static <R extends BaseEnum> R getByValue(Class<R> clazz, Object value, String errorMessage) {
        return Stream.of(clazz.getEnumConstants())
            .filter(baseEnum -> {
                if (value instanceof Enum<?>) {
                    return baseEnum.getValue().equals(value.toString());
                }
                return baseEnum.getValue().equals(value);
            })
            .findAny()
            .orElseThrow(() -> new ServiceException(errorMessage));
    }

    /**
     * 是否匹配
     *
     * @param value 枚举值
     * @return true
     */
    default boolean match(T value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Enum<?>) {
            return this.getValue().equals(value.toString());
        }
        return this.getValue().equals(value);
    }

    /**
     * 是否匹配
     *
     * @param baseEnum 枚举类
     * @return true
     */
    default boolean match(BaseEnum<T> baseEnum) {
        return Objects.equals(this, baseEnum);
    }

    /**
     * 枚举容器
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    class EnumContainer {

        /**
         * 枚举容器
         */
        private static final Map<BaseEnum, EnumBean> ENUM_MAP = new ConcurrentHashMap<>();

        /**
         * 添加枚举
         *
         * @param <T>      枚举值泛型
         * @param baseEnum 枚举
         */
        public static <T extends Serializable> void putEnum(BaseEnum<T> baseEnum) {
            String enumString = baseEnum.toString();
            ENUM_MAP.put(baseEnum, new EnumBean(enumString, enumString, enumString));
        }

        /**
         * 添加枚举
         *
         * @param <T>      枚举值泛型
         * @param baseEnum 枚举
         * @param value    枚举值
         * @param msg      枚举描述
         */
        public static <T extends Serializable> void putEnum(BaseEnum<T> baseEnum, T value, String msg) {
            if (value instanceof Enum<?>) {
                ENUM_MAP.put(baseEnum, new EnumBean(value.toString(), baseEnum.toString(), msg));
            } else {
                ENUM_MAP.put(baseEnum, new EnumBean(value, baseEnum.toString(), msg));
            }
        }

        /**
         * 获取枚举
         *
         * @param <T>      枚举值泛型
         * @param <K>      枚举类型
         * @param baseEnum 枚举
         * @return 枚举
         */
        static <K extends BaseEnum<T>, T extends Serializable> EnumBean<T> getEnum(K baseEnum) {
            return ENUM_MAP.get(baseEnum);
        }
    }

    /**
     * 枚举对象
     *
     * @param <T> 枚举值泛型
     */
    @Getter
    @AllArgsConstructor
    class EnumBean<T extends Serializable> {

        /**
         * 枚举值
         */
        private T value;

        /**
         * 枚举名称
         */
        private String name;

        /**
         * 枚举描述
         */
        private String msg;
    }
}
