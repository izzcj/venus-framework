package com.ale.venus.core.mybatis;

import com.ale.venus.common.enumeration.BaseEnum;
import com.ale.venus.common.utils.CastUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 枚举类型处理器
 *
 * @param <T> 枚举值类型
 * @param <E> 枚举类型
 * @author Ale
 * @version 1.0.0
 */
public class EnumTypeHandler<T extends Serializable, E extends BaseEnum<T>> extends BaseTypeHandler<E> {

    /**
     * 枚举类
     */
    private final Class<E> type;

    public EnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("枚举类型不能为空！");
        }
        if (!BaseEnum.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("枚举必须实现BaseEnum接口！");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter.getValue());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        T value = CastUtils.cast(rs.getObject(columnName));
        if (value == null) {
            return null;
        }
        return BaseEnum.getByValue(this.type, value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        T value = CastUtils.cast(rs.getObject(columnIndex));
        if (value == null) {
            return null;
        }

        return BaseEnum.getByValue(this.type, value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        T value = CastUtils.cast(cs.getObject(columnIndex));
        if (value == null) {
            return null;
        }

        return BaseEnum.getByValue(this.type, value);
    }
}
