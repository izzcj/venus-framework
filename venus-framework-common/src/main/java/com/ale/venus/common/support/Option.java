package com.ale.venus.common.support;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 选项信息
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Option implements Serializable {

    @Serial
    private static final long serialVersionUID = -2315021864326811753L;

    /**
     * 选项名称
     */
    private String label;

    /**
     * 选项值
     */
    private Serializable value;

    /**
     * 选项描述
     */
    private String description;

    /**
     * 选项排序
     */
    private Integer sequence;

    /**
     * 构建一个新的选项对象
     *
     * @param label 名称
     * @param value 值
     * @return 选项对象
     */
    public static Option of(String label, Serializable value) {
        return of(label, value, null, null);
    }

    /**
     * 构建一个新的选项对象
     *
     * @param label 名称
     * @param value 值
     * @param description 描述
     * @return 选项对象
     */
    public static Option of(String label, Serializable value, String description) {
        return of(label, value, description, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Option option = (Option) o;
        return Objects.equal(this.label, option.label) && Objects.equal(this.value, option.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.label, this.value);
    }
}
