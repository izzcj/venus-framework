package com.ale.venus.core.im;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * IM消息
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstantMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1213245L;

    /**
     * 消息来源
     */
    private String from;

    /**
     * 消息目标
     */
    private String to;

    /**
     * 消息类型
     */
    private InstantMessageType type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 额外属性
     */
    private Map<String, Object> extra;

}
