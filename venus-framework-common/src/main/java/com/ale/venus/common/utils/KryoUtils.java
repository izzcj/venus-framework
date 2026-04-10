package com.ale.venus.common.utils;

import cn.hutool.core.util.RuntimeUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.util.Pool;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 序列化工具类
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KryoUtils {

    /**
     * Kryo对象池
     */
    private static final Pool<Kryo> POOL = new Pool<>(true, false, RuntimeUtil.getProcessorCount() * 2) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            kryo.setInstantiatorStrategy(
                new DefaultInstantiatorStrategy(
                    new StdInstantiatorStrategy()
                )
            );
            return kryo;
        }
    };

    /**
     * Output对象池
     */
    private static final Pool<Output> OUTPUT_POOL = new Pool<>(true, false, RuntimeUtil.getProcessorCount() * 2) {
        @Override
        protected Output create() {
            return new Output(4096, 8192);
        }
    };

    /**
     * Input对象池
     */
    private static final Pool<Input> INPUT_POOL = new Pool<>(true, false, RuntimeUtil.getProcessorCount() * 2) {
        @Override
        protected Input create() {
            return new Input(8192);
        }
    };

    /**
     * 序列化对象
     *
     * @param value 对象
     * @return 字节
     */
    public static byte[] serialize(Object value) {
        Kryo kryo = POOL.obtain();
        Output output = OUTPUT_POOL.obtain();

        try {
            kryo.writeClassAndObject(output, value);
            return output.toBytes();
        } finally {
            output.reset();
            POOL.free(kryo);
            OUTPUT_POOL.free(output);
        }
    }

    /**
     * 序列化对象
     *
     * @param value 对象
     * @param outputStream 输出流
     */
    public static void serialize(Object value, OutputStream outputStream) {
        Kryo kryo = POOL.obtain();
        Output output = OUTPUT_POOL.obtain();

        try {
            output.setOutputStream(outputStream);
            kryo.writeClassAndObject(output, value);
        } finally {
            output.flush();
            output.reset();
            POOL.free(kryo);
            OUTPUT_POOL.free(output);
        }
    }

    /**
     * 反序列化对象
     *
     * @param bytes 字节
     * @return 对象
     * @param <T> 对象类型
     */
    public static <T> T deserialize(byte[] bytes) {
        Kryo kryo = POOL.obtain();
        Input input = INPUT_POOL.obtain();

        try {
            input.setBuffer(bytes);
            return CastUtils.cast(
                kryo.readClassAndObject(input)
            );
        } finally {
            input.reset();
            POOL.free(kryo);
            INPUT_POOL.free(input);
        }
    }

    /**
     * 反序列化对象
     *
     * @param inputStream 输入流
     * @return 对象
     * @param <T> 对象类型
     */
    public static <T> T deserialize(InputStream inputStream) {
        Kryo kryo = POOL.obtain();
        Input input = INPUT_POOL.obtain();

        try {
            input.setInputStream(inputStream);
            return CastUtils.cast(
                kryo.readClassAndObject(input)
            );
        } finally {
            input.reset();
            POOL.free(kryo);
            INPUT_POOL.free(input);
        }
    }

    /**
     * 获取一个Kryo对象
     *
     * @return Kryo对象
     */
    public static Kryo obtainKryo() {
        return POOL.obtain();
    }

    /**
     * 获取一个Output对象
     *
     * @return Output对象
     */
    public static Output obtainOutput() {
        return OUTPUT_POOL.obtain();
    }

    /**
     * 获取一个Input对象
     *
     * @return Input对象
     */
    public static Input obtainInput() {
        return INPUT_POOL.obtain();
    }

    /**
     * 还回一个Kryo对象
     *
     * @param kryo Kryo对象
     */
    public static void freeKryo(Kryo kryo) {
        POOL.free(kryo);
    }

    /**
     * 还回一个Output对象
     *
     * @param output Output对象
     */
    public static void freeOutput(Output output) {
        OUTPUT_POOL.free(output);
    }

    /**
     * 还回一个Input对象
     *
     * @param input Input对象
     */
    public static void freeInput(Input input) {
        INPUT_POOL.free(input);
    }
}
