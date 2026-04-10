package com.ale.venus.workflow.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.ale.venus.workflow.enumeration.FlowInstanceState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * 历史流程实例
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class FlowHistoryInstance extends FlowInstance {

    /**
     * 流程实例状态
     */
    private String state;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 处理耗时
     */
    private Long duration;

    /**
     * 删除状态
     */
    private Boolean deleted;

    /**
     * 创建历史流程实例
     *
     * @param instance 流程实例
     * @param state    状态
     * @return 历史流程实例
     */
    public static FlowHistoryInstance of(FlowInstance instance, FlowInstanceState state) {
        FlowHistoryInstance historyInstance = BeanUtil.copyProperties(instance, FlowHistoryInstance.class);
        historyInstance.setState(state.getValue());
        if (state != FlowInstanceState.ACTIVE) {
            historyInstance.setEndTime(LocalDateTime.now());
            historyInstance.setDuration(
                LocalDateTimeUtil.between(instance.getCreatedAt(), historyInstance.getEndTime())
                    .getSeconds()
            );
        }
        historyInstance.setDeleted(false);
        historyInstance.setUpdatedAt(LocalDateTime.now());
        historyInstance.setUpdatedBy(instance.getUpdatedBy());
        return historyInstance;
    }

}
