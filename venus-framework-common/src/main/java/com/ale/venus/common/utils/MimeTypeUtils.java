package com.ale.venus.common.utils;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.cache.CacheManager;
import com.ale.venus.common.constants.StringConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 媒体类型工具类
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MimeTypeUtils {

    /**
     * 默认媒体类型
     */
    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
    /**
     * 媒体类型映射
     */
    private static final CacheManager<String, String> MIME_TYPE_MAPPING_CACHE = CacheManager.newCache(MimeTypeUtils.class);

    static {
        // 微软 Office Word 格式（Microsoft Word 97 - 2004 document）
        MIME_TYPE_MAPPING_CACHE.set("doc", "application/msword");
        // 微软 Office Excel 格式（Microsoft Excel 97 - 2004 Workbook
        MIME_TYPE_MAPPING_CACHE.set("xls", "application/vnd.ms-excel");
        // 微软 Office PowerPoint 格式（Microsoft PowerPoint 97 - 2003 演示文稿）
        MIME_TYPE_MAPPING_CACHE.set("ppt", "application/vnd.ms-powerpoint");
        // 微软 Office Word 文档格式
        MIME_TYPE_MAPPING_CACHE.set("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        // 微软 Office Excel 文档格式
        MIME_TYPE_MAPPING_CACHE.set("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        // 微软 Office PowerPoint 文稿格式
        MIME_TYPE_MAPPING_CACHE.set("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        // GZ 压缩文件格式
        MIME_TYPE_MAPPING_CACHE.set("gz", "application/x-gzip");
        MIME_TYPE_MAPPING_CACHE.set("gzip", "application/x-gzip");
        // ZIP 压缩文件格式
        MIME_TYPE_MAPPING_CACHE.set("zip", "application/zip");
        MIME_TYPE_MAPPING_CACHE.set("7zip", "application/zip");
        // RAR 压缩文件格式
        MIME_TYPE_MAPPING_CACHE.set("rar", "application/rar");
        // TAR 压缩文件格式
        MIME_TYPE_MAPPING_CACHE.set("tar", "application/x-tar");
        MIME_TYPE_MAPPING_CACHE.set("tgz", "application/x-tar");
        // PDF 是 Portable Document Format 的简称，即便携式文档格式
        MIME_TYPE_MAPPING_CACHE.set("pdf", "application/pdf");
        // RTF 是指 Rich Text Format，即通常所说的富文本格式
        MIME_TYPE_MAPPING_CACHE.set("rtf", "application/rtf");
        // GIF 图像格式
        MIME_TYPE_MAPPING_CACHE.set("gif", "image/gif");
        // JPG(JPEG) 图像格式
        MIME_TYPE_MAPPING_CACHE.set("jpg", "image/jpeg");
        MIME_TYPE_MAPPING_CACHE.set("jpeg", "image/jpeg");
        // JPG2 图像格式
        MIME_TYPE_MAPPING_CACHE.set("jpg2", "image/jp2");
        // PNG 图像格式
        MIME_TYPE_MAPPING_CACHE.set("png", "image/png");
        // TIF(TIFF) 图像格式
        MIME_TYPE_MAPPING_CACHE.set("tif", "image/tiff");
        MIME_TYPE_MAPPING_CACHE.set("tiff", "image/tiff");
        // BMP 图像格式（位图格式）
        MIME_TYPE_MAPPING_CACHE.set("bmp", "image/bmp");
        // WebP 图像格式
        MIME_TYPE_MAPPING_CACHE.set("webp", "image/webp");
        // SVG 图像格式
        MIME_TYPE_MAPPING_CACHE.set("svg", "image/svg+xml");
        MIME_TYPE_MAPPING_CACHE.set("svgz", "image/svg+xml");
        // ico 图像格式，通常用于浏览器 Favicon 图标
        MIME_TYPE_MAPPING_CACHE.set("ico", "image/x-icon");
        // 金山 Office 文字排版文件格式
        MIME_TYPE_MAPPING_CACHE.set("wps", "application/kswps");
        // 金山 Office 表格文件格式
        MIME_TYPE_MAPPING_CACHE.set("et", "application/kset");
        // 金山 Office 演示文稿格式
        MIME_TYPE_MAPPING_CACHE.set("dps", "application/ksdps");
        // Photoshop 源文件格式
        MIME_TYPE_MAPPING_CACHE.set("psd", "application/x-photoshop");
        // Coreldraw 源文件格式
        MIME_TYPE_MAPPING_CACHE.set("cdr", "application/x-coreldraw");
        // Adobe Flash 源文件格式
        MIME_TYPE_MAPPING_CACHE.set("swf", "application/x-shockwave-flash");
        // 普通文本格式
        MIME_TYPE_MAPPING_CACHE.set("txt", "text/plain");
        // 表示 CSS 样式表
        MIME_TYPE_MAPPING_CACHE.set("css", "text/css");
        // 表示 Javascript 脚本文件
        // application/x-javascript	js	Javascript 文件类型
        MIME_TYPE_MAPPING_CACHE.set("js", "text/javascript");
        // HTML 文件格式
        MIME_TYPE_MAPPING_CACHE.set("htm", "text/html");
        MIME_TYPE_MAPPING_CACHE.set("html", "text/html");
        MIME_TYPE_MAPPING_CACHE.set("shtml", "text/html");
        // XHTML 文件格式
        MIME_TYPE_MAPPING_CACHE.set("xht", "application/xhtml+xml");
        MIME_TYPE_MAPPING_CACHE.set("xhtml", "application/xhtml+xml");
        // XML 文件格式
        MIME_TYPE_MAPPING_CACHE.set("xml", "text/xml");
        // VCF 文件格式
        MIME_TYPE_MAPPING_CACHE.set("vcf", "text/x-vcard");
        // PHP 文件格式
        MIME_TYPE_MAPPING_CACHE.set("php", "application/x-httpd-php");
        MIME_TYPE_MAPPING_CACHE.set("php3", "application/x-httpd-php");
        MIME_TYPE_MAPPING_CACHE.set("php4", "application/x-httpd-php");
        MIME_TYPE_MAPPING_CACHE.set("phtml", "application/x-httpd-php");
        // Java 归档文件格式
        MIME_TYPE_MAPPING_CACHE.set("jar", "application/java-archive");
        // Android 平台包文件格式
        MIME_TYPE_MAPPING_CACHE.set("apk", "application/vnd.android.package-archive");
        // Windows 系统可执行文件格式
        MIME_TYPE_MAPPING_CACHE.set("exe", "application/octet-stream");
        // PEM 文件格式
        MIME_TYPE_MAPPING_CACHE.set("crt", "application/x-x509-user-cert");
        MIME_TYPE_MAPPING_CACHE.set("pem", "application/x-x509-user-cert");
        // mpeg 音频格式
        MIME_TYPE_MAPPING_CACHE.set("mp3", "audio/mpeg");
        // mid 音频格式
        MIME_TYPE_MAPPING_CACHE.set("mid", "audio/midi");
        MIME_TYPE_MAPPING_CACHE.set("midi", "audio/midi");
        // wav 音频格式
        MIME_TYPE_MAPPING_CACHE.set("wav", "audio/x-wav");
        // m3u 音频格式
        MIME_TYPE_MAPPING_CACHE.set("m3u", "audio/x-mpegurl");
        // m4a 音频格式
        MIME_TYPE_MAPPING_CACHE.set("m4a", "audio/x-m4a");
        // ogg 音频格式
        MIME_TYPE_MAPPING_CACHE.set("ogg", "audio/ogg");
        // Real Audio 音频格式
        MIME_TYPE_MAPPING_CACHE.set("ra", "audio/x-realaudio");
        // mp4 视频格式
        MIME_TYPE_MAPPING_CACHE.set("mp4", "video/mp4");
        // mpeg 视频格式
        MIME_TYPE_MAPPING_CACHE.set("mpg", "video/mpeg");
        MIME_TYPE_MAPPING_CACHE.set("mpe", "video/mpeg");
        MIME_TYPE_MAPPING_CACHE.set("mpeg", "video/mpeg");
        // QuickTime 视频格式
        MIME_TYPE_MAPPING_CACHE.set("qt", "video/quicktime");
        MIME_TYPE_MAPPING_CACHE.set("mov", "video/quicktime");
        // m4v 视频格式
        MIME_TYPE_MAPPING_CACHE.set("m4v", "video/x-m4v");
        // wmv 视频格式（Windows 操作系统上的一种视频格式）
        MIME_TYPE_MAPPING_CACHE.set("wmv", "video/x-ms-wmv");
        // avi 视频格式
        MIME_TYPE_MAPPING_CACHE.set("avi", "video/x-msvideo");
        // webm 视频格式
        MIME_TYPE_MAPPING_CACHE.set("webm", "video/webm");
        // 一种基于 flash 技术的视频格式
        MIME_TYPE_MAPPING_CACHE.set("flv", "video/x-flv");
    }

    /**
     * 媒体类型结果类
     *
     * @param mimeType    媒体类型
     * @param fileContent 文件内容流，如果流无法重置，则返回新的流，否则就是参数传入的流
     */
    public record MimeTypeResult(String mimeType, InputStream fileContent) {
    }

    /**
     * 文件拓展类型结果类
     *
     * @param extension   拓展类型
     * @param fileContent 文件内容流，如果流无法重置，则返回新的流，否则就是参数传入的流
     */
    public record FileExtensionResult(String extension, InputStream fileContent) {
    }

    /**
     * 文件信息结果类
     *
     * @param filename 文件名称
     * @param mimeType 文件Mime类型
     * @param fileContent 文件内容流
     */
    public record FileInfoResult(String filename, String mimeType, InputStream fileContent) {
    }

    /**
     * 推断一个文件流的Mime类型
     *
     * @param fileContent 文件内容流
     * @return Mime结果对象
     */
    public static MimeTypeResult deduceFileMimeType(InputStream fileContent) {
        FileExtensionResult result = getFileExtension(fileContent);

        return new MimeTypeResult(
            getMimeType(result.extension),
            result.fileContent
        );
    }

    /**
     * 获取文件拓展类型
     *
     * @param fileContent 文件内容流
     * @return 文件拓展类型
     */
    public static FileExtensionResult getFileExtension(InputStream fileContent) {
        boolean supportsReset = false;
        try {
            fileContent.reset();
            supportsReset = true;
        } catch (IOException ignored) {
            // 不支持重置
        }

        if (supportsReset) {
            return new FileExtensionResult(
                deduceFileExtension(fileContent),
                fileContent
            );
        }

        // 不支持，就需要转换了
        byte[] bytes = IoUtil.readBytes(fileContent);
        var contentStream = new ByteArrayInputStream(bytes);

        return new FileExtensionResult(
            deduceFileExtension(contentStream),
            contentStream
        );
    }

    /**
     * 推断文件拓展类型
     *
     * @param fileContent 文件内容流
     * @return 文件拓展类型
     */
    private static String deduceFileExtension(InputStream fileContent) {
        String extension = FileTypeUtil.getType(fileContent, true);
        try {
            fileContent.reset();
        } catch (IOException ignored) {
        }

        return extension;
    }

    /**
     * 获取文件媒体类型
     *
     * @param fileExtension 文件拓展名
     * @return 媒体类型
     */
    public static String getMimeType(String fileExtension) {
        if (StrUtil.isBlank(fileExtension)) {
            return DEFAULT_MIME_TYPE;
        }

        return MIME_TYPE_MAPPING_CACHE.getOrDefault(fileExtension, DEFAULT_MIME_TYPE);
    }

    /**
     * 透视简单的文件信息
     *
     * @param originFilename 文件原始名称
     * @param fileContent 文件内容流
     * @return 文件信息结果对象
     */
    public static FileInfoResult introspectFileInfo(String originFilename, InputStream fileContent) {
        String ext = FileNameUtil.extName(originFilename);
        String name = FileNameUtil.mainName(originFilename);

        if (StrUtil.isBlank(ext)) {
            // 拓展名不存在，则尝试推断
            FileExtensionResult result = getFileExtension(fileContent);
            ext = result.extension;
            fileContent = result.fileContent;
        }

        if (StrUtil.isBlank(ext)) {
            // 依然为空，则无法得知文件拓展名
            ext = StringConstants.EMPTY;
        }

        return new FileInfoResult(
            name + StringConstants.DOT + ext,
            getMimeType(ext),
            fileContent
        );
    }
}
