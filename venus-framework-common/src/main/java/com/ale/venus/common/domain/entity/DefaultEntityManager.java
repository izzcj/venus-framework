package com.ale.venus.common.domain.entity;

import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.common.utils.ClassUtils;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.type.ClassMetadata;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认实现
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class DefaultEntityManager implements EntityManager, InitializingBean {

    /**
     * 实体扫描基路径
     */
    private final String[] basePackages;

    /**
     * 实体初始化器
     */
    private final EntityInitializers entityInitializers;

    /**
     * 实体类集合
     */
    private Set<Class<? extends BaseEntity>> entityClasses;

    @Override
    public boolean containsEntity(Class<? extends BaseEntity> entityClass) {
        return this.entityClasses.contains(entityClass);
    }

    @Override
    public Set<Class<? extends BaseEntity>> getEntityClasses() {
        return this.entityClasses;
    }

    @Override
    public Set<Class<? extends BaseEntity>> getAnnotatedEntityClasses(Class<? extends Annotation> annotationClass) {
        return this.entityClasses.stream()
            .filter(entityClass -> entityClass.isAnnotationPresent(annotationClass))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<Class<? extends BaseEntity>> classSet = Sets.newHashSetWithExpectedSize(64);
        for (String basePackage : this.basePackages) {
            classSet.addAll(
                CastUtils.cast(
                    ClassUtils.scanPackageClasses(basePackage, metadataReader -> {
                        ClassMetadata classMetadata = metadataReader.getClassMetadata();
                        return metadataReader.getAnnotationMetadata().hasAnnotation(TableName.class.getName())
                            && classMetadata.isConcrete()
                            && BaseEntity.class.isAssignableFrom(
                                ClassUtils.loadClass(classMetadata.getClassName())
                            );
                    })
                )
            );
        }

        this.entityClasses = Collections.unmodifiableSet(classSet);
        this.initialize();
    }

    /**
     * 实体类初始化
     */
    private void initialize() {
        for (Class<? extends BaseEntity> entityClass : this.entityClasses) {
            this.entityInitializers
                .getInitializers()
                .orderedStream()
                .forEach(entityInitializer -> entityInitializer.initialize(entityClass));
        }
    }
}
