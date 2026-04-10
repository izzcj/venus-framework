package com.ale.venus.core.mybatis;

import com.ale.venus.common.utils.SecurityUtils;
import com.ale.venus.core.constants.DataBaseConstants;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.function.Supplier;

/**
 * 审计元数据处理器
 *
 * @author Ale
 * @version 1.0.0
 */
public class AuditMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasGetter(DataBaseConstants.DELETED_FIELD_NAME)) {
            this.strictInsertFill(metaObject, DataBaseConstants.DELETED_FIELD_NAME, Boolean.class, false);
        }
        if (metaObject.hasGetter(DataBaseConstants.CREATE_TIME_CAMEL_FIELD_NAME)) {
            this.strictInsertFill(metaObject, DataBaseConstants.CREATE_TIME_CAMEL_FIELD_NAME, LocalDateTime.class, LocalDateTime.now());
        }
        if (metaObject.hasGetter(DataBaseConstants.UPDATE_TIME_CAMEL_FIELD_NAME)) {
            this.strictInsertFill(metaObject, DataBaseConstants.UPDATE_TIME_CAMEL_FIELD_NAME, LocalDateTime.class, LocalDateTime.now());
        }
        if (metaObject.hasGetter(DataBaseConstants.CREATE_BY_CAMEL_FIELD_NAME)) {
            this.strictInsertFill(metaObject, DataBaseConstants.CREATE_BY_CAMEL_FIELD_NAME, Long.class, SecurityUtils.getLoginUserId());
        }
        if (metaObject.hasGetter(DataBaseConstants.UPDATE_BY_CAMEL_FIELD_NAME)) {
            this.strictInsertFill(metaObject, DataBaseConstants.UPDATE_BY_CAMEL_FIELD_NAME, Long.class, SecurityUtils.getLoginUserId());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasGetter(DataBaseConstants.UPDATE_TIME_CAMEL_FIELD_NAME)) {
            this.strictUpdateFill(metaObject, DataBaseConstants.UPDATE_TIME_CAMEL_FIELD_NAME, LocalDateTime.class, LocalDateTime.now());
        }
        if (metaObject.hasGetter(DataBaseConstants.UPDATE_BY_CAMEL_FIELD_NAME)) {
            this.strictUpdateFill(metaObject, DataBaseConstants.UPDATE_BY_CAMEL_FIELD_NAME, Long.class, SecurityUtils.getLoginUserId());
        }
    }

    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        Object fieldValue = fieldVal.get();
        if (fieldValue != null) {
            metaObject.setValue(fieldName, fieldValue);
        }
        return this;
    }
}
