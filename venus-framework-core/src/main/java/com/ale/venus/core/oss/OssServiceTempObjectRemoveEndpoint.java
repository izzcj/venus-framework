package com.ale.venus.core.oss;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.domain.Result;
import com.ale.venus.common.support.RequestMethodMatcher;
import com.ale.venus.common.support.RequestUriMatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;

/**
 * OSS服务临时对象移除处理端点
 *
 * @author Ale
 * @version 1.0.0
 */
public class OssServiceTempObjectRemoveEndpoint extends AbstractOssEndpoint {

    /**
     * 获取OSS对象服务实现的移除临时上传对象的URL路径
     */
    private static final String OSS_TEMP_OBJECT_REMOVAL_PATH = "/oss";

    public OssServiceTempObjectRemoveEndpoint(ObjectProvider<OssService> ossServices, OssMateService ossMateService) {
        super(ossServices, ossMateService);
    }

    @Override
    public RequestMethodMatcher getRequestMethodMatcher() {
        return method -> HttpMethod.DELETE == method;
    }

    @Override
    public RequestUriMatcher getRequestUriMatcher() {
        return uri -> StrUtil.equals(OSS_TEMP_OBJECT_REMOVAL_PATH, uri);
    }

    @Override
    public Result<?> handleRequest(HttpServletRequest request, HttpServletResponse response) {
        String fileId = request.getParameter("fileId");
        if (StrUtil.isBlank(fileId)) {
            return JsonResult.fail("要下载的文件的ID不能为空");
        }

        OssMate ossMate = super.ossMateService.load(fileId);
        if (ossMate == null) {
            return JsonResult.fail("要移除的文件[{}]不存在！", fileId);
        }

        if (ossMate.getReferenceCount() == 0) {
            super.findOssService(ossMate.getProvider()).remove(ossMate.getObjectKey());
            super.ossMateService.remove(fileId);
            return JsonResult.success("临时文件移除成功");
        }
        return JsonResult.success();
    }
}
