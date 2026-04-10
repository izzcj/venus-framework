package com.ale.venus.core.oss;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.domain.Result;
import com.ale.venus.common.support.RequestMethodMatcher;
import com.ale.venus.common.support.RequestUriMatcher;
import com.ale.venus.common.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.io.InputStream;

/**
 * OSS对象服务下载端点
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public final class OssServiceDownloadEndpoint extends AbstractOssEndpoint {

    /**
     * OSS对象存储下载URL路径
     */
    private static final String OSS_DOWNLOAD_PATH = "/oss/download";

    public OssServiceDownloadEndpoint(ObjectProvider<OssService> ossServices, OssMateService ossMateService) {
        super(ossServices, ossMateService);
    }

    @Override
    public RequestMethodMatcher getRequestMethodMatcher() {
        return method -> HttpMethod.GET == method;
    }

    @Override
    public RequestUriMatcher getRequestUriMatcher() {
        return uri -> StrUtil.equals(OSS_DOWNLOAD_PATH, uri);
    }

    @Override
    public Result<?> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileId = request.getParameter("fileId");
        if (StrUtil.isBlank(fileId)) {
            return JsonResult.fail("要下载的文件的ID不能为空");
        }

        boolean multiDownload = fileId.indexOf(StringConstants.COMMA) > 0;
        if (multiDownload) {
            String[] fileIds = fileId.split(StringConstants.COMMA);
            for (String key : fileIds) {
                if (StrUtil.isBlank(key)) {
                    return JsonResult.fail("要下载的对象文件的Key存在空值");
                }
            }
            return this.doMultiDownload(fileIds, response);
        } else {
            OssMate ossMate = super.ossMateService.load(fileId);
            if (ossMate == null) {
                return JsonResult.fail("要下载的文件[{}]不存在！", fileId);
            }
            return this.doSingleDownload(
                    super.findOssService(ossMate.getProvider()),
                    ossMate.getObjectKey(),
                    response
            );
        }
    }

    /**
     * 单文件下载
     *
     * @param ossService OssService
     * @param objectKey  对象Key
     * @param response   响应对象
     * @return 结果对象
     * @throws IOException IO异常
     */
    private JsonResult<?> doSingleDownload(OssService ossService, String objectKey, HttpServletResponse response) throws IOException {
        try (
            InputStream inputStream = ossService.download(objectKey)
        ) {
            ServletUtils.responseFile(
                response,
                FileNameUtil.getName(objectKey),
                inputStream
            );
        }

        return null;
    }

    /**
     * 多文件下载
     *
     * @param fileIds  文件ID数组
     * @param response 响应对象
     * @return 结果对象
     * @throws IOException IO异常
     */
    private JsonResult<?> doMultiDownload(String[] fileIds, HttpServletResponse response) throws IOException {
        InputStream[] inputStreams = new InputStream[fileIds.length];
        String[] names = new String[fileIds.length];
        int index = 0;

        try {
            for (String fileId : fileIds) {
                OssMate ossMate = super.ossMateService.load(fileId);
                if (ossMate == null) {
                    return JsonResult.fail("要下载的文件[{}]不存在！", fileId);
                }
                inputStreams[index] = super.findOssService(ossMate.getProvider()).download(fileId);
                names[index++] = FileNameUtil.getName(fileId);
            }
            ServletUtils.responseFile(
                response,
                "download.zip",
                os -> ZipUtil.zip(
                    os,
                    names,
                    inputStreams
                )
            );
        } finally {
            for (InputStream inputStream : inputStreams) {
                inputStream.close();
            }
        }

        return null;
    }
}
