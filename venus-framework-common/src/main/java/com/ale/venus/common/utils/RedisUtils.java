package com.ale.venus.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.exception.UtilException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * redis工具类
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisUtils {

    /**
     * 值
     */
    private static final String LOCKING_VALUE = "1";

    /**
     * Redis锁存储键名
     */
    private static final String LOCKING_KEY_PREFIX = CacheUtils.buildCacheKeyWithPrefix("locking");

    /**
     * RedisTemplate
     */
    private static RedisTemplate<String, Object> redisTemplate;

    /**
     * StringRedisTemplate
     */
    private static StringRedisTemplate stringRedisTemplate;

    /**
     * 初始化RedisUtils工具类，设置RedisTemplate
     *
     * @param redisTemplate RedisTemplate对象
     */
    static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    /**
     * 初始化RedisUtils工具类，设置StringRedisTemplate
     *
     * @param stringRedisTemplate StringRedisTemplate对象
     */
    static void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        RedisUtils.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 设置一个值 不限时长
     *
     * @param key 键
     * @param value 值
     */
    public static void set(String key, Object value) {
        set(key, value, null);
    }

    /**
     * 设置一个值
     *
     * @param key 键
     * @param value 值
     * @param duration 存活时间
     */
    public static void set(String key, Object value, Duration duration) {
        if (duration == null || duration.isZero()) {
            // 不限时长
            redisTemplate.opsForValue().set(key, value);
            return;
        }
        redisTemplate.opsForValue().set(key, value, duration);
    }

    /**
     * 设置一个字符串值 不限时长
     *
     * @param key 键
     * @param value 值
     */
    public static void setString(String key, String value) {
        set(key, value, null);
    }

    /**
     * 设置一个字符串值
     *
     * @param key 键
     * @param value 值
     * @param duration 存活时间
     */
    public static void setString(String key, String value, Duration duration) {
        if (duration == null || duration.isZero()) {
            // 不限时长
            stringRedisTemplate.opsForValue().set(key, value);
            return;
        }
        stringRedisTemplate.opsForValue().set(key, value, duration);
    }

    /**
     * 获取一个值
     *
     * @param key 键
     * @param <T> 泛型
     * @return 值
     */
    public static <T> T get(String key) {
        return CastUtils.cast(redisTemplate.opsForValue().get(key));
    }

    /**
     * 获取一个值
     *
     * @param key 键
     * @param <T> 泛型
     * @param defaultValue 默认值
     * @return 值
     */
    public static <T> T get(String key, T defaultValue) {
        T value = get(key);
        return value == null ? defaultValue : value;
    }

    /**
     * 获取一个值
     *
     * @param key 键
     * @param supplier 默认值
     * @param <T> 泛型
     * @return 值
     */
    public static <T> T getIfAbsent(String key, Supplier<T> supplier) {
        T value = get(key);
        return value == null ? supplier.get() : value;
    }

    /**
     * 计算一个对应Key的值
     *
     * @param key 键
     * @param mappingFunction 映射函数
     * @return 值
     * @param <T> 泛型
     */
    public static <T> T compute(String key, Function<String, T> mappingFunction) {
        return compute(key, mappingFunction, null);
    }

    /**
     * 计算一个对应Key的值
     *
     * @param key 键
     * @param mappingFunction 映射函数
     * @param duration 存活时间
     * @return 值
     * @param <T> 泛型
     */
    public static <T> T compute(String key, Function<String, T> mappingFunction, Duration duration) {
        T value = get(key);
        if (value == null) {
            value = mappingFunction.apply(key);
            if (value != null) {
                set(key, value, duration);
            }
        }

        return value;
    }

    /**
     * 获取一个字符串值
     *
     * @param key 键
     * @return 值
     */
    public static String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取一个字符串值
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String getString(String key, String defaultValue) {
        String value = getString(key);
        return value == null ? defaultValue : value;
    }

    /**
     * 获取一个字符串值
     *
     * @param key 键
     * @param supplier 默认值
     * @return 值
     */
    public static String getStringIfAbsent(String key, Supplier<String> supplier) {
        String value = getString(key);
        return value == null ? supplier.get() : value;
    }

    /**
     * 计算一个对应Key的字符串值
     *
     * @param key 键
     * @param mappingFunction 映射函数
     * @return 值
     */
    public static String computeString(String key, Function<String, String> mappingFunction) {
        return computeString(key, mappingFunction, null);
    }

    /**
     * 计算一个对应Key的字符串值
     *
     * @param key 键
     * @param mappingFunction 映射函数
     * @param duration 存活时间
     * @return 值
     */
    public static String computeString(String key, Function<String, String> mappingFunction, Duration duration) {
        String value = getString(key);
        if (value == null) {
            value = mappingFunction.apply(key);
            if (value != null) {
                setString(key, value, duration);
            }
        }

        return value;
    }

    /**
     * 对一个值进行自增
     *
     * @param key 键
     * @param delta 增量
     */
    public static void increment(String key, long delta) {
        stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 对一个值进行自增
     *
     * @param key 键
     */
    public static void increment(String key) {
        increment(key, 1L);
    }

    /**
     * 根据前缀获取多个Redis键
     *
     * @param keyPrefix 键前缀
     * @return 键集合
     */
    @NonNull
    public static Set<String> getKeys(String keyPrefix) {
        Set<String> keys = redisTemplate.keys(keyPrefix + StringConstants.ASTERISK);

        if (CollectionUtil.isEmpty(keys)) {
            return Collections.emptySet();
        }

        return keys;
    }

    /**
     * 获取多个Redis值
     *
     * @param keyPrefix key前缀
     * @param <T> 泛型
     * @return 值列表
     */
    @NonNull
    public static <T> List<T> batchGet(String keyPrefix) {
        Set<String> keys = getKeys(keyPrefix);
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object> result = redisTemplate.opsForValue().multiGet(keys);
        if (CollectionUtil.isEmpty(result)) {
            return Collections.emptyList();
        }

        return CastUtils.cast(result);
    }

    /**
     * 获取多个Redis字符串值
     *
     * @param keyPrefix key前缀
     * @return 值列表
     */
    @NonNull
    public static List<String> batchGetString(String keyPrefix) {
        Set<String> keys = getKeys(keyPrefix);
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = stringRedisTemplate.opsForValue().multiGet(keys);
        if (CollectionUtil.isEmpty(result)) {
            return Collections.emptyList();
        }

        return result;
    }

    /**
     * 删除一个键
     *
     * @param key 键
     */
    public static void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除Key
     *
     * @param keyPrefix key前缀
     */
    @SuppressWarnings("unchecked")
    public static void batchDelete(String keyPrefix) {
        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public Object execute(@NonNull RedisOperations operations) throws DataAccessException {
                Set<String> keys = operations.keys(keyPrefix + StringConstants.ASTERISK);
                if (CollectionUtil.isNotEmpty(keys)) {
                    operations.delete(keys);
                }
                return null;
            }
        });
    }

    /**
     * 如果键不存在，则设置它
     *
     * @param key 键
     * @param value 值
     * @param duration 存活时间
     * @return 设置成功返回true
     */
    public static boolean setIfAbsent(String key, Object value, Duration duration) {
        if (duration == null || duration.isZero()) {
            // 不限时长
            return BooleanUtil.isTrue(redisTemplate.opsForValue().setIfAbsent(key, value));
        }
        return BooleanUtil.isTrue(redisTemplate.opsForValue().setIfAbsent(key, value, duration));
    }

    /**
     * 如果键不存在，则设置它
     *
     * @param key 键
     * @param value 值
     * @return 设置成功返回true
     */
    public static boolean setIfAbsent(String key, Object value) {
        return setIfAbsent(key, value, null);
    }

    /**
     * 如果键不存在，则设置它
     *
     * @param key 键
     * @param value 值
     * @param duration 存活时间
     * @return 设置成功返回true
     */
    public static boolean setStringIfAbsent(String key, String value, Duration duration) {
        if (duration == null || duration.isZero()) {
            // 不限时长
            return BooleanUtil.isTrue(stringRedisTemplate.opsForValue().setIfAbsent(key, value));
        }
        return BooleanUtil.isTrue(stringRedisTemplate.opsForValue().setIfAbsent(key, value, duration));
    }

    /**
     * 如果键不存在，则设置它
     *
     * @param key 键
     * @param value 值
     * @return 设置成功返回true
     */
    public static boolean setStringIfAbsent(String key, String value) {
        return setStringIfAbsent(key, value, null);
    }

    /**
     * 获取HashMap操作
     *
     * @param key 键
     * @param <HK> hash键
     * @param <HV> hash值
     * @return 操作对象
     */
    public static <HK, HV> BoundHashOperations<String, HK, HV> getBoundHashOperations(String key) {
        return redisTemplate.boundHashOps(key);
    }

    /**
     * 获取HashMap操作
     *
     * @param key 键
     * @return 操作对象
     */
    public static BoundHashOperations<String, String, String> getBoundStringHashOperations(String key) {
        return stringRedisTemplate.boundHashOps(key);
    }

    /**
     * 获取key剩余时间
     *
     * @param key 键名
     * @return 剩余时间（秒）
     */
    public static long getExpiredSeconds(String key) {
        Long seconds = redisTemplate.getExpire(key);
        return seconds == null ? 0 : seconds;
    }

    /**
     * 设置键的过期时间
     *
     * @param key 键名
     * @param expiration 过期时长
     * @return bool
     */
    public static boolean setExpiration(String key, Duration expiration) {
        var result = redisTemplate.expire(key, expiration);
        return result != null && result;
    }

    /**
     * 设置键的过期日期
     *
     * @param key 键名
     * @param date 过期日期
     * @return bool
     */
    public static boolean setExpirationAt(String key, Date date) {
        var result = redisTemplate.expireAt(key, date);
        return result != null && result;
    }

    /**
     * 判断Redis中是否有某一个Key
     *
     * @param key 键名
     * @return bool
     */
    public static boolean has(String key) {
        return BooleanUtil.isTrue(redisTemplate.hasKey(key));
    }

    /**
     * 获取锁
     *
     * @param key                锁标识
     * @param waitTimeoutSeconds 超时时间
     * @param expiredSeconds     锁过期时间（防止死锁）
     * @return 如果获取锁成功则返回true，否则返回false
     */
    @SuppressWarnings("BusyWait")
    public static boolean lock(String key, int waitTimeoutSeconds, int expiredSeconds) {
        long timeout = waitTimeoutSeconds * 1000L;
        long currentTime = System.currentTimeMillis();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        while (System.currentTimeMillis() - currentTime <= timeout) {
            if (setStringIfAbsent(CacheUtils.buildCacheKey(LOCKING_KEY_PREFIX, key), LOCKING_VALUE, Duration.ofSeconds(expiredSeconds))) {
                // 获取锁成功
                return true;
            }
            if (Thread.interrupted()) {
                return false;
            }
            // 获锁不成功
            try {
                Thread.sleep(20, random.nextInt(40));
            } catch (InterruptedException e) {
                log.warn("获取不到Redis锁，等待过程中休眠出现异常：{}", e.getMessage(), e);
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    /**
     * 取消锁
     *
     * @param key 键
     */
    public static void unlock(String key) {
        delete(CacheUtils.buildCacheKey(LOCKING_KEY_PREFIX, key));
    }

    /**
     * 带锁执行一段代码
     *
     * @param key 锁key
     * @param callable 逻辑调用
     * @param <R> 执行器返回类型
     * @return 返回值
     */
    public static <R> R doWithLock(String key, Callable<R> callable) {
        if (StrUtil.isBlank(key)) {
            throw new UtilException("Redis锁的key不能为空");
        }
        try {
            // 并发加锁
            boolean acquired = lock(key, 30, 120);
            if (!acquired) {
                log.warn("获取Redis锁[{}]失败", key);
                // 抢占锁失败
                throw new UtilException("获取Redis锁「" + key + "」失败");
            }
            // 执行
            return callable.call();
        } catch (Exception e) {
            throw new UtilException(e.getMessage(), e);
        } finally {
            unlock(key);
        }
    }

    /**
     * 批量执行操作
     *
     * @param operationsExecutor 操作执行器
     */
    @SuppressWarnings("unchecked")
    public static void batchExecute(Consumer<Operations> operationsExecutor) {
        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public Object execute(@NonNull RedisOperations operations) throws DataAccessException {
                operationsExecutor.accept(
                    new Operations(operations)
                );
                return null;
            }
        });
    }

    /**
     * Redis操作封装类
     */
    @RequiredArgsConstructor
    public static class Operations {

        /**
         * Redis操作对象
         */
        private final RedisOperations<String, Object> redisOperations;

        /**
         * 获取一个值
         *
         * @param key 键
         * @return 值
         * @param <T> 泛型
         */
        public <T> T get(String key) {
            return CastUtils.cast(
                this.redisOperations.opsForValue().get(key)
            );
        }

        /**
         * 设置一个值
         *
         * @param key 键
         * @param value 值
         * @param duration 过期时长
         */
        public void set(String key, Object value, Duration duration) {
            this.redisOperations.opsForValue().set(key, value, duration);
        }

        /**
         * 删除一个值
         *
         * @param key 键
         */
        public void delete(String key) {
            this.redisOperations.delete(key);
        }

        /**
         * 判断键值是否存在
         *
         * @param key 键
         * @return bool
         */
        public boolean has(String key) {
            return BooleanUtil.isTrue(this.redisOperations.hasKey(key));
        }
    }
}
