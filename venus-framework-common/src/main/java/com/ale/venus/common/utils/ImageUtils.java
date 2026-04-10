package com.ale.venus.common.utils;

import com.ale.venus.common.exception.UtilException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片处理工具类
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImageUtils {

    /**
     * 压缩图片
     *
     * @param inputStream 输入流
     * @return 压缩后的图片输入流
     */
    public static ByteArrayOutputStream compressImage(InputStream inputStream) {
        var outputStream = new ByteArrayOutputStream();

        try {
            Thumbnails.of(inputStream)
                .outputQuality(0.5)
                .scale(0.8)
                .toOutputStream(outputStream);

            return outputStream;
        } catch (IOException e) {
            throw new UtilException("压缩图片失败: {}", e.getMessage(), e);
        }
    }
}
