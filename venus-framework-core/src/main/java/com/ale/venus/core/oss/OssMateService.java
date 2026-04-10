package com.ale.venus.core.oss;

import java.util.Collection;
import java.util.List;

/**
 * Oss元信息服务
 *
 * @author Ale
 * @version 1.0.0
 */
public interface OssMateService {

    /**
     * 保存Oss元信息
     *
     * @param ossMate Oss元信息
     */
    void save(OssMate ossMate);

    /**
     * 批量保存Oss元信息
     *
     * @param ossMates Oss元信息集合
     */
    void batchSave(Collection<OssMate> ossMates);

    /**
     * 更新Oss元信息
     *
     * @param ossMate Oss元信息
     */
    void update(OssMate ossMate);

    /**
     * 批量更新Oss元信息
     *
     * @param ossMates Oss元信息集合
     */
    void batchUpdate(Collection<OssMate> ossMates);

    /**
     * 加载Oss元信息
     *
     * @param id ID
     * @return Oss元信息
     */
    OssMate load(String id);

    /**
     * 加载Oss元信息集合
     *
     * @param ids ID集合
     * @return Oss元信息集合
     */
    List<OssMate> load(Collection<String> ids);

    /**
     * 根据URL加载Oss元信息
     *
     * @param url URL
     * @return Oss元信息
     */
    OssMate loadByUrl(String url);

    /**
     * 根据URL集合加载Oss元信息
     *
     * @param urls URL集合
     * @return Oss元信息集合
     */
    List<OssMate> loadByUrls(Collection<String> urls);

    /**
     * 删除Oss元信息
     *
     * @param id ID
     */
    void remove(String id);

    /**
     * 批量删除Oss元信息
     *
     * @param ids ID集合
     */
    void batchRemove(Collection<String> ids);
}
