package com.ale.venus.core.oss;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.domain.Result;
import com.ale.venus.common.support.RequestMethodMatcher;
import com.ale.venus.common.support.RequestUriMatcher;
import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * OSS服务存储端点
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public final class OssServiceUploadEndpoint extends AbstractOssEndpoint {

    /**
     * OSS对象存储上传路径模式
     */
    private static final String OSS_UPLOAD_PATH_PATTERN = "/oss/upload/{ossProvider}/{fileType}";

    /**
     * 支持的文件类型
     */
    private static final Set<String> SUPPORTED_FILE_TYPES = Set.of("image", "file");

    /**
     * 路径匹配器
     */
    private final PathMatcher pathMatcher = new AntPathMatcher();

    public OssServiceUploadEndpoint(ObjectProvider<OssService> ossServices, OssMateService ossMateService) {
        super(ossServices, ossMateService);
    }

    @Override
    public RequestMethodMatcher getRequestMethodMatcher() {
        return method -> HttpMethod.POST == method;
    }


    @Override
    public RequestUriMatcher getRequestUriMatcher() {
        return uri -> this.pathMatcher.match(OSS_UPLOAD_PATH_PATTERN, uri);
    }

    @Override
    public Result<?> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> variables = this.pathMatcher.extractUriTemplateVariables(
            OSS_UPLOAD_PATH_PATTERN,
            request.getRequestURI()
        );
        String ossProvider = variables.get("ossProvider");
        if (StrUtil.isBlank(ossProvider)) {
            return JsonResult.fail("OSS实现类型不能为空");
        }
        OssServiceProvider serviceProvider;
        try {
            serviceProvider = OssServiceProvider.valueOf(ossProvider.toUpperCase());
        } catch (IllegalArgumentException e) {
            return JsonResult.fail("不支持的OSS实现类型：{}", ossProvider);
        }
        String fileType = variables.get("fileType");
        if (!SUPPORTED_FILE_TYPES.contains(fileType)) {
            return JsonResult.fail("不支持的文件类型：{}", fileType);
        }
        return this.handleUpload(fileType + StringConstants.SLASH, super.findOssService(serviceProvider), serviceProvider, request);
    }

    /**
     * 处理文件上传
     *
     * @param objectKeyPrefix 对象Key前缀
     * @param ossService      OSS服务实现
     * @param provider        OSS实现提供器
     * @param request         请求对象
     * @return 结果对象
     * @throws IOException 异常
     */
    private JsonResult<?> handleUpload(String objectKeyPrefix, OssService ossService, OssServiceProvider provider, HttpServletRequest request) throws IOException {
        if (!MultipartResolutionDelegate.isMultipartRequest(request)) {
            return JsonResult.fail("非上传文件请求");
        }

        // 在过滤器中可能request尚未被包装为MultipartHttpServletRequest，因此需要手动包装一下
        MultipartHttpServletRequest multipartRequest =
            Objects.requireNonNullElseGet(
                WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class),
                () -> new StandardMultipartHttpServletRequest(request)
            );
        MultiValueMap<String, MultipartFile> multiFileMap = multipartRequest.getMultiFileMap();
        List<Map<String, String>> result = Lists.newArrayList();
        for (Map.Entry<String, List<MultipartFile>> entry : multiFileMap.entrySet()) {
            for (MultipartFile file : entry.getValue()) {
                if (file.getSize() == 0) {
                    continue;
                }

                String objectKey = ossService.upload(
                    objectKeyPrefix,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getInputStream()
                );

                OssMate ossMate = this.saveOssMate(ossService, provider, objectKey, file.getContentType());
                result.add(
                    Map.of(
                        "id", ossMate.getId(),
                        "url", ossMate.getUrl()
                    )
                );
                log.info("{} - 文件[{}]上传成功，文件对象Key：{}，文件大小：{}", entry.getKey(), file.getOriginalFilename(), objectKey, DataSizeUtil.format(file.getSize()));
            }
        }

        if (result.isEmpty()) {
            return JsonResult.fail("上传文件为空，请检查");
        }

        if (result.size() > 1) {
            return JsonResult.success(result);
        }

        return JsonResult.success(result.getFirst());
    }

    /**
     * 保存OSS元信息
     *
     * @param ossService OSS服务实现
     * @param provider   OSS实现提供器
     * @param objectKey  对象Key
     * @param fileType   文件类型
     * @return Oss元信息
     */
    private OssMate saveOssMate(OssService ossService, OssServiceProvider provider, String objectKey, String fileType) {
        OssMate ossMate = OssMate.builder()
            .provider(provider)
            .objectKey(objectKey)
            .mimeType(fileType)
            .size(ossService.getSize(objectKey))
            .url(ossService.getBaseUrl() + objectKey)
            .referenceCount(0)
            .createTime(LocalDateTime.now())
            .lastVisitTime(LocalDateTime.now())
            .build();
        super.ossMateService.save(ossMate);
        return ossMate;
    }
}
