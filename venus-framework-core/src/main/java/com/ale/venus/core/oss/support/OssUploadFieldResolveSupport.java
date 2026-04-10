package com.ale.venus.core.oss.support;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.domain.entity.BaseEntity;
import com.ale.venus.common.support.ReflectionField;
import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.common.utils.ReflectionUtils;
import com.ale.venus.core.oss.OssMate;
import com.ale.venus.core.oss.OssMateService;
import com.ale.venus.core.oss.OssUpload;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * OSS上传字段解析支持
 *
 * @author Ale
 * @version 1.0.0
 */
public abstract class OssUploadFieldResolveSupport {

    /**
     * Regex
     */
    private static final Pattern FILE_URL_PATTERN = Pattern.compile("(?:\\b(?:src|href)\\s*=\\s*)?([\"']?https?://[^\"'\\\\\\s>]+[\"']?)", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    /**
     * Oss元信息服务
     */
    protected final OssMateService ossMateService;

    public OssUploadFieldResolveSupport(OssMateService ossMateService) {
        this.ossMateService = ossMateService;
    }

    /**
     * 解析Oss上传字段
     *
     * @param entity 实体
     * @return Oss上传字段列表
     */
    protected List<ReflectionField> resolveOssUploadFields(BaseEntity entity) {
        return ReflectionUtils.getClassAnnotatedFields(entity.getClass(), OssUpload.class);
    }

    /**
     * 解析文件ID集合
     *
     * @param entity         实体
     * @param ossUploadField OSS上传字段
     * @return 文件ID集合
     */
    protected Set<String> resolveFileIds(BaseEntity entity, ReflectionField ossUploadField) {
        OssUpload ossUpload = ossUploadField.field().getAnnotation(OssUpload.class);
        if (entity == null) {
            return Collections.emptySet();
        }
        Object fieldValue = ossUploadField.getValue(entity);
        if (fieldValue == null) {
            return Collections.emptySet();
        }
        if (Collection.class.isAssignableFrom(fieldValue.getClass())) {
            return CastUtils.cast(fieldValue);
        }
        if (ossUpload.richText()) {
            Set<String> urls = this.parseUrls(CastUtils.cast(fieldValue));
            if (CollectionUtil.isEmpty(urls)) {
                return Collections.emptySet();
            }
            List<OssMate> ossMates = this.ossMateService.loadByUrls(urls);
            return ossMates.stream()
                .map(OssMate::getId)
                .collect(Collectors.toSet());
        }
        return Collections.singleton(CastUtils.cast(fieldValue));
    }

    /**
     * 解析URL
     *
     * @param value 值
     * @return 解析后的URL集合
     */
    private Set<String> parseUrls(String value) {
        Set<String> urls = new HashSet<>();
        Matcher matcher = FILE_URL_PATTERN.matcher(value);
        while (matcher.find()) {
            String url = matcher.group(matcher.groupCount());
            // 如果原始内容为markdown语法，url匹配后右边会多一个)，不能在正则中排除)，url格式可能为![alt](http://xxxxxx/a_(1).png)
            if (url.endsWith(StringConstants.RIGHT_BRACKET)) {
                urls.add(url.substring(0, url.length() - 1));
            } else {
                urls.add(url);
            }
        }

        return urls;
    }
}
