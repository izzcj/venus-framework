package com.ale.venus.common.utils;

import com.ale.venus.common.exception.UtilException;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Class工具类
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassUtils extends org.springframework.util.ClassUtils {

    /**
     * 资源加载器
     */
    private static ResourcePatternResolver resourcePatternResolver;

    /**
     * 元信息缓存工厂
     */
    private static MetadataReaderFactory metadataReaderFactory;

    /**
     * 设置ApplicationContext上下文
     *
     * @param applicationContext 上下文
     */
    static void setApplicationContext(final ApplicationContext applicationContext) {
        resourcePatternResolver = applicationContext;
        metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
    }

    /**
     * 加载Class类
     *
     * @param className 类全路径
     * @return 类
     */
    public static Class<?> loadClass(final String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new UtilException("Class[{}]不存在，加载失败：{}", e, className, e.getMessage());
        }
    }

    /**
     * 扫描某个包的所有的类
     *
     * @param classesPackage 包路径
     * @param filter 过滤器
     * @return 类集合
     */
    public static Set<Class<?>> scanPackageClasses(final String classesPackage, final Predicate<MetadataReader> filter) {
        log.info("开始扫描包路径：{}", classesPackage);
        Set<Class<?>> classes = Sets.newLinkedHashSet();
        String path = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + convertClassNameToResourcePath(classesPackage) + "/**/*.class";
        Resource[] resources;
        try {
            resources = resourcePatternResolver.getResources(path);
        } catch (IOException e) {
            log.warn("扫描包路径[{}]出现错误：{}", classesPackage, e.getMessage());
            return Collections.emptySet();
        }

        for (Resource resource : resources) {
            if (!resource.exists() || !resource.isReadable()) {
                continue;
            }
            MetadataReader metadataReader;
            try {
                metadataReader = metadataReaderFactory.getMetadataReader(resource);
            } catch (IOException e) {
                log.warn("读取Class[{}]信息出现错误：{}", resource.getDescription(), e.getMessage());
                continue;
            }
            if (filter != null && !filter.test(metadataReader)) {
                continue;
            }
            // 这里处理真正的类
            try {
                final Class<?> clazz = loadClass(metadataReader.getClassMetadata().getClassName());
                classes.add(clazz);
            } catch (UtilException ignored) {
            }
        }
        return classes;
    }
}
