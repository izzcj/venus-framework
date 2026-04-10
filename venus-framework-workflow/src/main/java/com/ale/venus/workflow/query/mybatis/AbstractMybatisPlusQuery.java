package com.ale.venus.workflow.query.mybatis;

import com.ale.venus.workflow.entity.FlowEntity;
import com.ale.venus.workflow.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 基于MybatisPlus的抽象查询构建器
 *
 * @param <T> 查询结果类型
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public abstract class AbstractMybatisPlusQuery<T extends FlowEntity> implements Query<T> {

    /**
     * 提供Mapper
     *
     * @return Mapper
     */
    protected abstract BaseMapper<T> provideMapper();

    /**
     * 构建wrapper
     *
     * @return wrapper
     */
    protected abstract QueryWrapper<T> buildQueryWrapper();

    @Override
    public T single() {
        List<T> resultList = this.provideMapper().selectList(this.buildQueryWrapper());
        if (resultList.size() == 1) {
            return resultList.get(0);
        } else if (resultList.size() > 1) {
            log.warn("查询结果数量大于1，默认返回第一条数据");
            return resultList.get(0);
        }
        return null;
    }

    @Override
    public List<T> list() {
        return this.provideMapper().selectList(this.buildQueryWrapper());
    }

    @Override
    public Page<T> page(Pageable pageable) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> historyTaskPage = this.provideMapper().selectPage(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageable.getPageNumber() + 1, pageable.getPageSize()),
            this.buildQueryWrapper()
        );
        return new PageImpl<>(historyTaskPage.getRecords(), pageable, historyTaskPage.getTotal());
    }
}
