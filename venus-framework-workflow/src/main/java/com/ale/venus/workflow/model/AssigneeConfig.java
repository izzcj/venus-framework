package com.ale.venus.workflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 受理人配置
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssigneeConfig implements Serializable {

    /**
     * 类型
     */
    private String type;

    /**
     * 值
     */
    private Object value;
}
