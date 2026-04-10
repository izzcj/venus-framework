package com.ale.venus.core.oss;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.domain.Result;
import com.ale.venus.common.support.RequestMethodMatcher;
import com.ale.venus.common.support.RequestUriMatcher;
import com.ale.venus.core.endpoint.Endpoint;
import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * OSS服务获取访问URL端点
 *
 * @author Ale
 * @version 1.0.0
 */
public class OssServiceVisitUrlEndpoint implements Endpoint {

    /**
     * 获取OSS对象访问URL的路径
     */
    private static final String OSS_VISIT_URL_PATH = "/oss/visit-url";

    /**
     * Oss元信息服务
     */
    private final OssMateService ossMateService;

    public OssServiceVisitUrlEndpoint(OssMateService ossMateService) {
        this.ossMateService = ossMateService;
    }

    @Override
    public RequestUriMatcher getRequestUriMatcher() {
        return uri -> StrUtil.equals(OSS_VISIT_URL_PATH, uri);
    }

    @Override
    public RequestMethodMatcher getRequestMethodMatcher() {
        return method -> HttpMethod.GET == method;
    }

    @Override
    public Result<?> handleRequest(HttpServletRequest request, HttpServletResponse response) {
        String fileId = request.getParameter("fileId");
        if (StrUtil.isBlank(fileId)) {
            return JsonResult.fail("要访问的文件的ID不能为空");
        }

        boolean multiVisit = fileId.indexOf(StringConstants.COMMA) > 0;
        if (multiVisit) {
            String[] fileIds = fileId.split(StringConstants.COMMA);
            List<String> urls = Lists.newArrayListWithExpectedSize(fileIds.length);
            for (String id : fileIds) {
                if (StrUtil.isBlank(id)) {
                    return JsonResult.fail("要访问的对象文件的Id存在空值");
                }
                OssMate ossMate = this.ossMateService.load(id);
                if (ossMate == null) {
                    return JsonResult.fail("要访问的文件[{}]不存在！", id);
                }
                urls.add(ossMate.getUrl());
            }
            return JsonResult.success(urls);
        } else {
            OssMate ossMate = this.ossMateService.load(fileId);
            if (ossMate == null) {
                return JsonResult.fail("要访问的文件[{}]不存在！", fileId);
            }
            return JsonResult.success(Result.GENERIC_SUCCESS_MSG, ossMate.getUrl());
        }
    }
}
