package com.ale.venus.core.service;

import com.ale.venus.common.domain.entity.BaseEntity;
import com.ale.venus.core.service.hook.ServiceHookExecutorSupport;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.MybatisUtils;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 基于MyBatisPlus的抽象基础服务实现类
 *
 * @param <M> Mapper类型
 * @param <E> 实体类型
 * @author Ale
 * @version 1.0.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMybatisPlusBaseServiceImpl<M extends BaseMapper<E>, E extends BaseEntity> extends ServiceHookExecutorSupport<E, AbstractMybatisPlusBaseServiceImpl<M, E>> implements IService<E> {

    /**
     * 日志
     */
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * @see #getEntityClass()
     */
    private Class<E> entityClass;

    /**
     *  @see #getMapperClass()
     */
    private Class<M> mapperClass;

    /**
     * SqlSessionFactory
     */
    private volatile SqlSessionFactory sqlSessionFactory;

    @Override
    public Class<E> getEntityClass() {
        if (this.entityClass == null) {
            this.entityClass = (Class<E>) GenericTypeUtils.resolveTypeArguments(this.getMapperClass(), BaseMapper.class)[0];
        }
        return this.entityClass;
    }

    /**
     * 获取SqlSessionFactory
     *
     * @return SqlSessionFactory
     */
    protected SqlSessionFactory getSqlSessionFactory() {
        if (this.sqlSessionFactory == null) {
            MybatisMapperProxy<?> mybatisMapperProxy = MybatisUtils.getMybatisMapperProxy(this.getBaseMapper());
            this.sqlSessionFactory = MybatisUtils.getSqlSessionFactory(mybatisMapperProxy);
        }
        return this.sqlSessionFactory;
    }

    /**
     * 获取Mapper真实类型
     *
     * @return baseMapper 真实类型
     * @since 3.5.7
     */
    public Class<M> getMapperClass() {
        if (this.mapperClass == null) {
            MybatisMapperProxy<?> mybatisMapperProxy = MybatisUtils.getMybatisMapperProxy(this.getBaseMapper());
            this.mapperClass = (Class<M>) mybatisMapperProxy.getMapperInterface();
        }
        return this.mapperClass;
    }

    @Override
    public boolean saveOrUpdate(E entity) {
        return getBaseMapper().insertOrUpdate(entity);
    }

    @Override
    public E getOne(Wrapper<E> queryWrapper, boolean throwEx) {
        return getBaseMapper().selectOne(queryWrapper, throwEx);
    }

    @Override
    public Optional<E> getOneOpt(Wrapper<E> queryWrapper, boolean throwEx) {
        return Optional.ofNullable(getBaseMapper().selectOne(queryWrapper, throwEx));
    }

    @Override
    public Map<String, Object> getMap(Wrapper<E> queryWrapper) {
        return SqlHelper.getObject(this.log, getBaseMapper().selectMaps(queryWrapper));
    }

    @Override
    public <V> V getObj(Wrapper<E> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(this.log, listObjs(queryWrapper, mapper));
    }

    /**
     * 执行批量操作
     *
     * @param list      数据集合
     * @param batchSize 批量大小
     * @param consumer  执行方法
     * @return 操作结果
     * @since 3.3.1
     */
    protected boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return SqlHelper.executeBatch(getSqlSessionFactory(), this.log, list, batchSize, consumer);
    }

    /**
     * 执行批量操作
     *
     * @param list     数据集合
     * @param consumer 执行方法
     * @return 操作结果
     * @since 3.3.1
     */
    protected boolean executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
        return executeBatch(list, DEFAULT_BATCH_SIZE, consumer);
    }

    @Override
    public boolean removeById(Serializable id, boolean useFill) {
        return SqlHelper.retBool(getBaseMapper().deleteById(id, useFill));
    }

}
