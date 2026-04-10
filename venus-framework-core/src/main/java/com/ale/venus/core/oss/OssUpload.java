package com.ale.venus.core.oss;

import java.lang.annotation.*;

/**
 * OSS文件上传
 *
 * @author Ale
 * @version 1.0.0
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OssUpload {

    /**
     * 是否为富文本字段
     *
     * @return 是否为富文本字段
     */
    boolean richText() default false;

    /**
     * 是否公共访问
     *
     * @return 是否公共访问
     */
    boolean publicAccess() default true;

}
