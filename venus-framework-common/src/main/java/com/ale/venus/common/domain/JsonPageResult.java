package com.ale.venus.common.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;

/**
 * json分页返回结果
 *
 * @param <T> 数据类型
 * @author Ale
 * @version 1.0.0
 */
public final class JsonPageResult<T> extends JsonResult<JsonPageResult.PageData<T>> {

    /**
     * 创建一个分页结果对象
     *
     * @param page    页码
     * @param size    分页大小
     * @param total   数据总数
     * @param content 数据内容
     * @param <T>     数据类型
     * @return 分页结果对象
     */
    public static <T> JsonPageResult<T> of(long page, long size, long total, List<T> content) {
        return new JsonPageResult<>(page, size, total, content);
    }

    /**
     * 创建一个分页结果对象
     *
     * @param page MybatisPlus分页对象
     * @param <T>  数据类型
     * @return 分页结果对象
     */
    public static <T> JsonPageResult<T> of(IPage<T> page) {
        return of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    /**
     * 创建一个分页结果对象
     *
     * @param page   MybatisPlus分页对象
     * @param mapper 数据映射函数
     * @param <T>    数据类型
     * @return 分页结果对象
     */
    public static <T> JsonPageResult<T> of(IPage<?> page, Function<List<?>, List<T>> mapper) {
        return of(page.getCurrent(), page.getSize(), page.getTotal(), mapper.apply(page.getRecords()));
    }

    /**
     * 创建一个分页结果对象
     *
     * @param page 标准分页对象
     * @param <T>  数据类型
     * @return 分页结果对象
     */
    public static <T> JsonPageResult<T> of(Page<T> page) {
        Pageable pageable = page.getPageable();
        return of(pageable.getPageNumber(), pageable.getPageSize(), page.getTotalElements(), page.getContent());
    }

    /**
     * 创建一个分页结果对象
     *
     * @param page   标准分页对象
     * @param mapper 数据映射函数
     * @param <T>    数据类型
     * @return 分页结果对象
     */
    public static <T> JsonPageResult<T> of(Page<?> page, Function<List<?>, List<T>> mapper) {
        Pageable pageable = page.getPageable();
        return of(pageable.getPageNumber(), pageable.getPageSize(), page.getTotalElements(), mapper.apply(page.getContent()));
    }

    private JsonPageResult(long page, long size, long total, List<T> data) {
        super(SUCCESS_CODE, GENERIC_SUCCESS_MSG, new PageData<>(page, size, total, data));
    }

    /**
     * 分页数据
     *
     * @param <T> 数据类型
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class PageData<T> {
        /**
         * 页码
         */
        private long page;
        /**
         * 分页大小
         */
        private long size;
        /**
         * 数据总数
         */
        private long total;
        /**
         * 数据内容
         */
        private List<T> data;
    }
}
