package com.ale.venus.workflow.hook;

import com.ale.venus.common.support.Supportable;
import com.ale.venus.workflow.entity.FlowInstance;

/**
 * 流程实例监听器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface InstanceListener extends Supportable<InstanceEventType> {

    /**
     * 流程实例事件通知
     *
     * @param eventType    事件类型
     * @param flowInstance 流程实例
     * @return 是否处理成功
     */
    boolean notify(InstanceEventType eventType, FlowInstance flowInstance);

}
