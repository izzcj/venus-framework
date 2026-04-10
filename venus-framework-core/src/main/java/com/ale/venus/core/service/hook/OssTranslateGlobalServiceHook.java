package com.ale.venus.core.service.hook;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.common.domain.entity.BaseEntity;
import com.ale.venus.common.support.ReflectionField;
import com.ale.venus.core.oss.OssMate;
import com.ale.venus.core.oss.OssMateService;
import com.ale.venus.core.oss.OssUpload;
import com.ale.venus.core.oss.support.OssUploadFieldResolveSupport;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 对象翻译全局钩子
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@Component
public class OssTranslateGlobalServiceHook extends OssUploadFieldResolveSupport implements GlobalServiceHook {

    public OssTranslateGlobalServiceHook(OssMateService ossMateService) {
        super(ossMateService);
    }

    @Override
    public void afterQuery(BaseEntity entity, HookContext context) {
        if (entity == null) {
            return;
        }
        this.processEntities(CollectionUtil.newArrayList(entity));
    }

    @Override
    public void afterQueryList(List<BaseEntity> entities, HookContext context) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        this.processEntities(entities);
    }

    /**
     * 处理实体集合
     *
     * @param entities 实体集合
     */
    private void processEntities(List<BaseEntity> entities) {
        if (CollectionUtil.isEmpty(entities)) {
            return;
        }
        // 找出需要处理的字段（只做一次反射）
        List<ReflectionField> ossFields = this.resolvePublicOssFields(entities.getFirst());
        if (ossFields.isEmpty()) {
            return;
        }

        // 收集所有 fileId
        Set<String> allIds = Sets.newHashSet();
        for (BaseEntity entity : entities) {
            for (ReflectionField field : ossFields) {
                allIds.addAll(super.resolveFileIds(entity, field));
            }
        }
        if (allIds.isEmpty()) {
            return;
        }

        // 查询元信息
        Map<String, String> urlMap = super.ossMateService.load(allIds).stream()
            .collect(Collectors.toMap(OssMate::getId, OssMate::getUrl));

        // 替换字段值
        for (BaseEntity entity : entities) {
            for (ReflectionField field : ossFields) {
                this.replaceFieldValue(entity, field, urlMap);
            }
        }
    }

    /**
     * 解析公共Oss上传字段
     *
     * @param entity 实体
     * @return 公共Oss上传字段列表
     */
    private List<ReflectionField> resolvePublicOssFields(BaseEntity entity) {
        return super.resolveOssUploadFields(entity).stream()
            .filter(reflectionField -> {
                OssUpload anno = reflectionField.field().getAnnotation(OssUpload.class);
                return !anno.richText() && anno.publicAccess();
            })
            .toList();
    }

    /**
     * 替换字段值
     *
     * @param entity 实体
     * @param field  字段
     * @param urlMap URL映射
     */
    private void replaceFieldValue(BaseEntity entity, ReflectionField field, Map<String, String> urlMap) {
        Object value = field.getValue(entity);
        switch (value) {
            case null -> {
                return;
            }
            // 单值
            case String id -> {
                field.setValue(entity, urlMap.getOrDefault(id, id));
                return;
            }
            // 集合
            case Collection<?> collection -> {
                Collection<String> newCollection = this.createSameTypeCollection(collection);

                for (Object idObj : collection) {
                    String id = String.valueOf(idObj);
                    newCollection.add(urlMap.getOrDefault(id, id));
                }
                field.setValue(entity, newCollection);
                return;
            }
            default -> {
            }
        }

        // 数组
        if (value.getClass().isArray()) {
            Object[] arr = (Object[]) value;
            String[] newArr = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                String id = String.valueOf(arr[i]);
                newArr[i] = urlMap.getOrDefault(id, id);
            }
            field.setValue(entity, newArr);
        }
    }

    /**
     * 创建相同类型的集合
     *
     * @param collection 原始集合
     * @return 新集合
     */
    private Collection<String> createSameTypeCollection(Collection<?> collection) {
        if (collection instanceof Set) {
            return new HashSet<>();
        }
        return new ArrayList<>();
    }
}
