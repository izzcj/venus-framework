package com.ale.venus.core.service.hook;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.common.domain.entity.BaseEntity;
import com.ale.venus.common.support.ReflectionField;
import com.ale.venus.core.constants.HookConstants;
import com.ale.venus.core.oss.*;
import com.ale.venus.core.oss.support.OssUploadFieldResolveSupport;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

/**
 * 对象上传全局钩子
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@Component
public class OssUploadGlobalServiceHook extends OssUploadFieldResolveSupport implements GlobalServiceHook {

    public OssUploadGlobalServiceHook(OssMateService ossMateService) {
        super(ossMateService);
    }

    @Override
    public void beforeSave(BaseEntity entity, HookContext context) {
        this.handleOssUpload(CollectionUtil.newArrayList(entity), context, false);
    }

    @Override
    public void beforeBatchSave(List<BaseEntity> entityList, HookContext context) {
        this.handleOssUpload(entityList, context, true);
    }

    @Override
    public void beforeDelete(BaseEntity entity, HookContext context) {
        List<ReflectionField> ossUploadFields = super.resolveOssUploadFields(entity);
        if (ossUploadFields.isEmpty()) {
            return;
        }
        Set<String> decreaseRefCountOssMateIds = Sets.newHashSet();
        ossUploadFields.forEach(field -> decreaseRefCountOssMateIds.addAll(this.resolveFileIds(entity, field)));
        TransactionSynchronizationManager.registerSynchronization(
            new OssMateUpdateTransactionSynchronization(
                super.ossMateService,
                Collections.emptySet(),
                decreaseRefCountOssMateIds
            )
        );
    }

    @Override
    public void beforeBatchDelete(List<BaseEntity> entityList, HookContext context) {
        List<ReflectionField> ossUploadFields = super.resolveOssUploadFields(entityList.getFirst());
        if (ossUploadFields.isEmpty()) {
            return;
        }
        Set<String> decreaseRefCountOssMateIds = Sets.newHashSet();
        for (BaseEntity entity : entityList) {
            ossUploadFields.forEach(field -> decreaseRefCountOssMateIds.addAll(this.resolveFileIds(entity, field)));
        }
        TransactionSynchronizationManager.registerSynchronization(
            new OssMateUpdateTransactionSynchronization(
                this.ossMateService,
                Collections.emptySet(),
                decreaseRefCountOssMateIds
            )
        );
    }

    /**
     * 处理Oss上传
     *
     * @param entities 实体列表
     * @param context  钩子上下文
     * @param isBatch  是否批量
     */
    private void handleOssUpload(List<BaseEntity> entities, HookContext context, boolean isBatch) {
        List<ReflectionField> ossUploadFields = super.resolveOssUploadFields(entities.getFirst());
        if (ossUploadFields.isEmpty()) {
            return;
        }

        Set<String> increaseRefCountOssMateIds = Sets.newHashSet();
        Set<String> decreaseRefCountOssMateIds = Sets.newHashSet();

        if (isBatch) {
            Map<Long, BaseEntity> oldEntityMap = context.getOrDefault(HookConstants.OLD_ENTITY_MAP_KEY, Collections.emptyMap());
            for (BaseEntity entity : entities) {
                BaseEntity oldEntity = oldEntityMap.get(entity.getId());
                this.processEntityFileIds(entity, oldEntity, ossUploadFields, increaseRefCountOssMateIds, decreaseRefCountOssMateIds);
            }
        } else {
            BaseEntity oldEntity = context.get(HookConstants.OLD_ENTITY_KEY);
            this.processEntityFileIds(entities.getFirst(), oldEntity, ossUploadFields, increaseRefCountOssMateIds, decreaseRefCountOssMateIds);
        }

        TransactionSynchronizationManager.registerSynchronization(
            new OssMateUpdateTransactionSynchronization(
                super.ossMateService,
                increaseRefCountOssMateIds,
                decreaseRefCountOssMateIds
            )
        );
    }

    /**
     * 处理实体文件ID
     *
     * @param entity                     实体
     * @param oldEntity                  旧实体
     * @param ossUploadFields            OSS上传字段列表
     * @param increaseRefCountOssMateIds 增加引用计数的OssMate ID集合
     * @param decreaseRefCountOssMateIds 减少引用计数的OssMate ID集合
     */
    private void processEntityFileIds(BaseEntity entity, BaseEntity oldEntity, List<ReflectionField> ossUploadFields, Set<String> increaseRefCountOssMateIds, Set<String> decreaseRefCountOssMateIds) {
        for (ReflectionField ossUploadField : ossUploadFields) {
            Set<String> currentFileIds = super.resolveFileIds(entity, ossUploadField);
            if (oldEntity == null) {
                increaseRefCountOssMateIds.addAll(currentFileIds);
                continue;
            }

            Set<String> oldFileIds = super.resolveFileIds(oldEntity, ossUploadField);
            if (currentFileIds.isEmpty()) {
                decreaseRefCountOssMateIds.addAll(oldFileIds);
                continue;
            }
            Set<String> addedFileIds = Sets.difference(currentFileIds, oldFileIds);
            Set<String> removedFileIds = Sets.difference(oldFileIds, currentFileIds);

            increaseRefCountOssMateIds.addAll(addedFileIds);
            decreaseRefCountOssMateIds.addAll(removedFileIds);
        }
    }

    /**
     * 元信息更新事务同步
     * 此逻辑使用@TransactionalEventListener也可以实现，但是会对外暴露Event和EventListener
     */
    @RequiredArgsConstructor
    static class OssMateUpdateTransactionSynchronization implements TransactionSynchronization {

        /**
         * Oss元信息服务
         */
        private final OssMateService ossMateService;

        /**
         * 增加引用计数的OssMate ID集合
         */
        private final Set<String> increaseRefCountOssMateIds;

        /**
         * 减少引用计数的OssMate ID集合
         */
        private final Set<String> decreaseRefCountOssMateIds;

        @Override
        public int getOrder() {
            return HIGHEST_PRECEDENCE;
        }

        @Override
        public void afterCommit() {
            this.increaseReferenceCount(this.increaseRefCountOssMateIds);
            this.decreaseReferenceCount(this.decreaseRefCountOssMateIds);
        }

        @Override
        public void afterCompletion(int status) {
            if (status == STATUS_COMMITTED) {
                return;
            }

            // 失败则回滚处理
            this.increaseReferenceCount(this.decreaseRefCountOssMateIds);
            this.decreaseReferenceCount(this.increaseRefCountOssMateIds);
        }

        /**
         * 增加引用计数
         *
         * @param ossMateIds OssMate ID集合
         */
        private void increaseReferenceCount(Set<String> ossMateIds) {
            if (CollectionUtil.isEmpty(ossMateIds)) {
                return;
            }
            List<OssMate> ossMates = this.ossMateService.load(ossMateIds);
            for (OssMate ossMate : ossMates) {
                ossMate.setReferenceCount(ossMate.getReferenceCount() + 1);
            }
            this.ossMateService.batchUpdate(ossMates);
        }

        /**
         * 减少引用计数
         *
         * @param ossMateIds Oss元信息ID集合
         */
        private void decreaseReferenceCount(Set<String> ossMateIds) {
            if (CollectionUtil.isEmpty(ossMateIds)) {
                return;
            }
            List<OssMate> ossMates = this.ossMateService.load(ossMateIds);
            for (OssMate ossMate : ossMates) {
                // 引用计数为0时不立即删除，由定时任务处理
                ossMate.setReferenceCount(Math.max(0, ossMate.getReferenceCount() - 1));
            }
            this.ossMateService.batchUpdate(ossMates);
        }
    }
}
