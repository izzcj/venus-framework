package com.ale.venus.core.convert;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.support.Option;
import com.ale.venus.common.support.ReflectionField;
import com.ale.venus.common.utils.ReflectionUtils;
import com.ale.venus.common.utils.ServletUtils;

import java.io.Serializable;

/**
 * Option转换接口
 *
 * @author Ale
 * @version 1.0.0
 */
public interface OptionConvertible {

    /**
     * 转换成Option
     *
     * @return Option
     */
    default Option convert() {
        Class<?> originalObjectClass = this.getClass();
        ReflectionField valueField = ReflectionUtils.getField(originalObjectClass, providerValueField());
        ReflectionField labelField = ReflectionUtils.getField(originalObjectClass, providerLabelField());
        ReflectionField descriptionField = ReflectionUtils.getField(originalObjectClass, providerDescriptionField());
        if (valueField == null || labelField == null) {
            return null;
        }
        Serializable value = valueField.getValue(this);
        if (value instanceof Long) {
            value = value.toString();
        }
        return Option.of(
            labelField.getValue(this),
            value,
            descriptionField == null ? null : descriptionField.getValue(this)
        );
    }

    /**
     * 提供value字段
     *
     * @return value
     */
    default String providerValueField() {
        String valueFiled = ServletUtils.getRequest().getParameter("valueFiled");
        if (StrUtil.isNotBlank(valueFiled)) {
            return valueFiled;
        }
        return "id";
    }

    /**
     * 提供label字段
     *
     * @return label
     */
    default String providerLabelField() {
        String labelFiled = ServletUtils.getRequest().getParameter("labelFiled");
        if (StrUtil.isNotBlank(labelFiled)) {
            return labelFiled;
        }
        return "name";
    }

    /**
     * 提供description字段
     *
     * @return description
     */
    default String providerDescriptionField() {
        String descriptionFiled = ServletUtils.getRequest().getParameter("descriptionFiled");
        if (StrUtil.isNotBlank(descriptionFiled)) {
            return descriptionFiled;
        }
        return null;
    }
}
