package com.ale.venus.core.translation;

import com.ale.venus.common.support.Supportable;
import java.util.Map;

/**
 * 通用翻译器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface GenericTranslator extends Supportable<String> {

    /**
     * 翻译
     *
     * @param type   数据类型
     * @param params 参数
     * @param value  值
     * @return 翻译后的值
     */
    String translate(String type, Map<String, Object> params, String value);

}
