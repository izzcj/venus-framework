package com.ale.venus.common.enumeration;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.common.utils.ClassUtils;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.type.ClassMetadata;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * 枚举扫描器
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class EnumScanner implements InitializingBean {

    /**
     * 扫描到的枚举类集合
     */
    protected static Set<Class<? extends BaseEnum<?>>> types;

    /**
     * 扫描的基包路径
     */
    private final String[] basePackages;

    /**
     * 枚举初始化器集合
     */
    private final EnumInitializers enumInitializers;

    @Override
    public void afterPropertiesSet() {
        if (types == null) {
            types = Sets.newHashSetWithExpectedSize(64);
            for (String basePackage : this.basePackages) {
                if (StrUtil.isBlank(basePackage)) {
                    continue;
                }

                types.addAll(
                    CastUtils.cast(
                        ClassUtils.scanPackageClasses(basePackage, metadataReader -> {
                            ClassMetadata classMetadata = metadataReader.getClassMetadata();
                            if (!classMetadata.isFinal() || !classMetadata.hasSuperClass() || !Objects.equals(classMetadata.getSuperClassName(), Enum.class.getName()) || classMetadata.getInterfaceNames().length == 0) {
                                return false;
                            }
                            return Arrays.stream(classMetadata.getInterfaceNames()).anyMatch(interfaceName -> Objects.equals(interfaceName, BaseEnum.class.getName()));
                        })
                    )
                );
            }
        }

        // 初始化
        this.initialize();
    }

    /**
     * 初始化枚举
     */
    private void initialize() {
        for (Class<? extends BaseEnum<?>> type : types) {
            this.enumInitializers.getInitializers()
                .orderedStream()
                .forEach(enumInitializer -> enumInitializer.initialize(type));
        }
    }
}
