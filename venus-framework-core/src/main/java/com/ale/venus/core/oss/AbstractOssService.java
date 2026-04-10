package com.ale.venus.core.oss;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.utils.MimeTypeUtils;

import java.io.InputStream;

/**
 * 抽象对象存储服务
 *
 * @author Ale
 * @version 1.0.0
 */
public abstract class AbstractOssService implements OssService {

    @Override
    public String upload(String objectKeyPrefix, String objectName, InputStream objectContent) {
        return this.upload(objectKeyPrefix, objectName, null, objectContent);
    }

    @Override
    public final String upload(String objectKeyPrefix, String objectName, String mimeType, InputStream objectContent) {
        StringBuilder objectKey = new StringBuilder(IdUtil.fastSimpleUUID().toUpperCase());
        if (StrUtil.isNotBlank(objectName)) {
            objectKey.append(StringConstants.UNDERSCORE)
                    .append(StringConstants.UNDERSCORE)
                    .append(objectName);
        }

        MimeTypeUtils.FileInfoResult fileInfo = MimeTypeUtils.introspectFileInfo(
                objectKey.toString(),
                objectContent
        );

        if (StrUtil.isNotBlank(objectKeyPrefix)) {
            objectKey = new StringBuilder(objectKeyPrefix)
                    .append(fileInfo.filename());
        }
        return doUpload(
                objectKey.toString(),
                fileInfo.fileContent(),
                StrUtil.isNotBlank(mimeType) && !StrUtil.equals(mimeType, fileInfo.mimeType()) ? mimeType : fileInfo.mimeType(),
                objectContent
        );
    }

    /**
     * 执行上传
     *
     * @param objectKey     对象Key
     * @param fileContent   文件内容
     * @param mimeType      Mime类型
     * @param objectContent 文件内容
     * @return 上传后的对象Key
     */
    protected abstract String doUpload(String objectKey, InputStream fileContent, String mimeType, InputStream objectContent);

}
