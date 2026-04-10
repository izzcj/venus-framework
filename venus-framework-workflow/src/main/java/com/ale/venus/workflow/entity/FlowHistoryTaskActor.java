package com.ale.venus.workflow.entity;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 历史流程任务参与人
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowHistoryTaskActor extends FlowTaskActor {

    /**
     * 评论
     */
    private String comment;

    /**
     * 附件，json格式
     */
    private String attachments;

    /**
     * 删除状态
     */
    private Boolean deleted;

    /**
     * 创建历史任务参与人
     *
     * @param taskActor 任务参与人
     * @return 历史任务参与人
     */
    public static FlowHistoryTaskActor of(FlowTaskActor taskActor) {
        FlowHistoryTaskActor historyTaskActor = BeanUtil.copyProperties(taskActor, FlowHistoryTaskActor.class);
        historyTaskActor.setDeleted(false);
        return historyTaskActor;
    }
}
