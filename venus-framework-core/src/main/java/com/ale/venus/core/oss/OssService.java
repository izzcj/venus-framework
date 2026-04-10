package com.ale.venus.core.oss;

import com.ale.venus.common.support.Supportable;

import java.io.InputStream;
import java.util.Collection;

/**
 * oss服务接口
 *
 * @author Ale
 * @version 1.0.0
 */
public interface OssService extends Supportable<OssServiceProvider> {

    /**
     * 临时对象Key前缀
     */
    String TEMPORARY_OBJECT_KEY_PREFIX = "temp/";

    /**
     * 获取访问文件的基URL
     *
     * @return 基URL
     */
    String getBaseUrl();

    /**
     * 获取bucket
     *
     * @return bucket
     */
    String getBucket();

    /**
     * 判断对象是否存在
     *
     * @param objectKey 对象Key
     * @return bool
     */
    boolean exists(String objectKey);

    /**
     * 获取对象文件大小
     *
     * @param objectKey 对象Key
     * @return 对象文件大小
     */
    long getSize(String objectKey);

    /**
     * 下载对象文件
     *
     * @param objectKey 对象Key
     * @return 输入流
     */
    InputStream download(String objectKey);

    /**
     * 上传对象文件
     *
     * @param objectKeyPrefix 对象Key前缀
     * @param objectName      对象名称
     * @param objectContent   输入流对象内容
     * @return 对象Key
     */
    String upload(String objectKeyPrefix, String objectName, InputStream objectContent);

    /**
     * 上传对象文件
     *
     * @param objectKeyPrefix 对象Key前缀
     * @param objectName      对象名称
     * @param mimeType        Mime类型
     * @param objectContent   输入流对象内容
     * @return 对象Key
     */
    String upload(String objectKeyPrefix, String objectName, String mimeType, InputStream objectContent);

    /**
     * 移动临时对象文件到永久目录下
     *
     * @param objectKey 对象Key
     * @return 移动后的对象Key
     */
    String moveToPermanentDirectory(String objectKey);

    /**
     * 移动永久对象文件到临时目录下
     *
     * @param objectKey 对象Key
     * @return 移动后的对象Key
     */
    String moveToTemporaryDirectory(String objectKey);

    /**
     * 移动对象文件
     *
     * @param sourceObjectKey 源对象文件Key
     * @param targetObjectKey 目的对象文件Key
     */
    void move(String sourceObjectKey, String targetObjectKey);

    /**
     * 删除对象文件
     *
     * @param objectKey 对象Key
     */
    void remove(String objectKey);

    /**
     * 批量删除对象文件
     *
     * @param objectKeys 对象Key集合
     */
    void batchRemove(Collection<String> objectKeys);

}
