package com.ale.venus.workflow.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.ale.venus.workflow.enumeration.FlowTaskState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 历史流程给任务
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class FlowHistoryTask extends FlowTask {

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
     * 创建历史任务
     *
     * @param flowTask 流程任务
     * @return 历史任务
     */
    public static FlowHistoryTask of(FlowTask flowTask) {
        return FlowHistoryTask.of(flowTask, FlowTaskState.COMPLETE);
    }

    /**
     * 创建历史任务
     *
     * @param task  流程任务
     * @param state 流程任务状态
     * @return 历史任务
     */
    public static FlowHistoryTask of(FlowTask task, FlowTaskState state) {
        FlowHistoryTask historyTask = BeanUtil.copyProperties(task, FlowHistoryTask.class);
        historyTask.setState(state.getValue());
        historyTask.setEndTime(LocalDateTime.now());
        historyTask.setDuration(LocalDateTimeUtil.between(historyTask.getCreatedAt(), historyTask.getEndTime(), ChronoUnit.SECONDS));
        historyTask.setDeleted(false);
        return historyTask;
    }
}
