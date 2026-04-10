package com.ale.venus.workflow.entity;

import com.ale.venus.workflow.enumeration.FlowDefinitionState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

/**
 * 流程定义
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class FlowDefinition extends FlowEntity {

    /**
     * key 唯一标识
     */
    private String definitionKey;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 名称
     */
    private String name;

    /**
     * 拼音简码
     */
    private String pinyinAbbr;

    /**
     * 图标地址
     */
    private String icon;

    /**
     * 流程版本
     */
    private Integer version;

    /**
     * 使用范围
     */
    private String useScope;

    /**
     * 使用范围配置，json格式
     */
    private String useScopeConfig;

    /**
     * 是否发布
     */
    private Boolean published;

    /**
     * 状态
     *
     * @see FlowDefinitionState
     */
    private String state;

    /**
     * 设计内容
     */
    private String designContent;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注说明
     */
    private String description;

    /**
     * 删除状态
     */
    private Boolean deleted;

    /**
     * 获取下一个版本
     *
     * @return 下一个版本
     */
    public int nextVersion() {
        return this.version + 1;
    }
}
