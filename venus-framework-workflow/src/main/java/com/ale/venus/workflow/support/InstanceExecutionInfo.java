package com.ale.venus.workflow.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.enumeration.BaseEnum;
import com.ale.venus.workflow.entity.*;
import com.ale.venus.workflow.enumeration.AssigneeType;
import com.ale.venus.workflow.enumeration.FlowExecutionState;
import com.ale.venus.workflow.enumeration.FlowInstanceState;
import com.ale.venus.workflow.enumeration.FlowTaskState;
import com.google.common.collect.Lists;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程实例执行信息
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
public class InstanceExecutionInfo {

    /**
     * 流程实例ID
     */
    private String id;

    /**
     * 流程实例状态
     */
    private FlowInstanceState state;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 执行信息
     */
    private List<ExecutionInfo> executions;

    /**
     * 创建流程实例执行信息
     *
     * @param historyInstance   流程实例
     * @param executions        执行信息
     * @param tasks             任务信息
     * @param taskActors        任务参与人信息
     * @param historyTasks      历史任务信息
     * @param historyTaskActors 历史任务参与人信息
     * @return 流程实例执行信息
     */
    public static InstanceExecutionInfo of(FlowHistoryInstance historyInstance,
                                           List<FlowExecution> executions,
                                           List<FlowTask> tasks,
                                           List<FlowTaskActor> taskActors,
                                           List<FlowHistoryTask> historyTasks,
                                           List<FlowHistoryTaskActor> historyTaskActors) {
        InstanceExecutionInfo instanceExecutionInfo = new InstanceExecutionInfo();
        instanceExecutionInfo.setId(historyInstance.getId());
        instanceExecutionInfo.setState(BaseEnum.getByValue(FlowInstanceState.class, historyInstance.getState()));
        instanceExecutionInfo.setCreatedBy(historyInstance.getCreatedBy());
        instanceExecutionInfo.setCreatedAt(historyInstance.getCreatedAt());
        List<FlowExecution> topExecutions = Lists.newArrayListWithCapacity(executions.size());
        // parentId用于区分是否为分支下的执行记录，因此分组的key为lastExecutionId
        Map<String, List<FlowExecution>> childrenExecutionMapping = executions.stream()
            .filter(execution -> {
                if (StrUtil.isBlank(execution.getParentId())) {
                    topExecutions.add(execution);
                    return false;
                }
                return true;
            })
            .collect(Collectors.groupingBy(FlowExecution::getLastExecutionId));
        List<FlowHistoryTask> activeTasks = BeanUtil.copyToList(tasks, FlowHistoryTask.class);
        historyTasks.addAll(activeTasks);
        List<FlowHistoryTaskActor> activeTaskActors = BeanUtil.copyToList(taskActors, FlowHistoryTaskActor.class);
        historyTaskActors.addAll(activeTaskActors);
        Map<String, List<FlowHistoryTask>> historyTaskMapping = historyTasks.stream()
            .collect(Collectors.groupingBy(FlowHistoryTask::getExecutionId));
        Map<String, List<FlowHistoryTaskActor>> historyTaskActorMapping = historyTaskActors.stream()
            .collect(Collectors.groupingBy(FlowHistoryTaskActor::getExecutionId));
        List<ExecutionInfo> executionInfos = Lists.newArrayListWithCapacity(topExecutions.size());
        for (FlowExecution topExecution : topExecutions) {
            executionInfos.add(
                ExecutionInfo.of(
                    topExecution,
                    childrenExecutionMapping,
                    historyTaskMapping,
                    historyTaskActorMapping
                )
            );
        }
        instanceExecutionInfo.setExecutions(executionInfos);
        return instanceExecutionInfo;
    }

    /**
     * 执行信息
     */
    @Data
    static class ExecutionInfo {

        /**
         * 执行记录ID
         */
        private String id;

        /**
         * 创建时间
         */
        private LocalDateTime createdAt;

        /**
         * 流程实例ID
         */
        private String instanceId;

        /**
         * 节点ID
         */
        private String nodeId;

        /**
         * 节点类型
         */
        private String nodeType;

        /**
         * 节点名称
         */
        private String nodeName;

        /**
         * 是否完成
         */
        private Boolean completed;

        /**
         * 执行状态
         */
        private FlowExecutionState state;

        /**
         * 完成时间
         */
        private LocalDateTime completedTime;

        /**
         * 子执行信息
         */
        private List<ExecutionInfo> children;

        /**
         * 任务信息
         */
        private List<TaskInfo> tasks;

        /**
         * 创建执行信息
         *
         * @param execution               执行记录
         * @param childrenMapping         子执行记录映射
         * @param historyTaskMapping      历史任务映射
         * @param historyTaskActorMapping 历史任务参与者映射
         * @return 执行信息
         */
        public static ExecutionInfo of(FlowExecution execution,
                                       Map<String, List<FlowExecution>> childrenMapping,
                                       Map<String, List<FlowHistoryTask>> historyTaskMapping,
                                       Map<String, List<FlowHistoryTaskActor>> historyTaskActorMapping) {
            ExecutionInfo executionInfo = new ExecutionInfo();
            executionInfo.setId(execution.getId());
            executionInfo.setCreatedAt(execution.getCreatedAt());
            executionInfo.setInstanceId(execution.getInstanceId());
            executionInfo.setNodeId(execution.getNodeId());
            executionInfo.setNodeName(execution.getNodeName());
            executionInfo.setNodeType(execution.getNodeType());
            executionInfo.setCompleted(FlowExecutionState.COMPLETE.match(execution.getState()));
            executionInfo.setCompletedTime(execution.getCompletedTime());
            List<FlowExecution> children = childrenMapping.get(execution.getId());
            if (CollectionUtil.isNotEmpty(children)) {
                List<ExecutionInfo> childrenInfo = Lists.newArrayListWithCapacity(children.size());
                for (FlowExecution child : children) {
                    childrenInfo.add(ExecutionInfo.of(child, childrenMapping, historyTaskMapping, historyTaskActorMapping));
                }
                executionInfo.setChildren(childrenInfo);
            }
            executionInfo.setTasks(TaskInfo.of(historyTaskMapping.get(execution.getId()), historyTaskActorMapping.get(execution.getId())));
            return executionInfo;
        }
    }

    /**
     * 任务信息
     */
    @Data
    static class TaskInfo {

        /**
         * 任务ID
         */
        private String id;

        /**
         * 受理人ID
         */
        private String assigneeId;

        /**
         * 期望完成时间
         */
        private LocalDateTime expireTime;

        /**
         * 任务状态
         */
        private FlowTaskState state;

        /**
         * 是否已阅
         */
        private Boolean viewed;

        /**
         * 任务完成时间
         */
        private LocalDateTime endTime;

        /**
         * 任务耗时
         */
        private Long duration;

        /**
         * 任务参与人信息
         */
        private List<TaskActorInfo> actors;

        /**
         * 构建任务信息
         *
         * @param historyTasks      历史任务
         * @param historyTaskActors 历史任务参与人
         * @return 任务信息
         */
        public static List<TaskInfo> of(List<FlowHistoryTask> historyTasks, List<FlowHistoryTaskActor> historyTaskActors) {
            if (CollectionUtil.isEmpty(historyTasks) || CollectionUtil.isEmpty(historyTaskActors)) {
                return Collections.emptyList();
            }
            Map<String, List<FlowHistoryTaskActor>> historyTaskActorsMapping = historyTaskActors.stream()
                .collect(Collectors.groupingBy(FlowHistoryTaskActor::getTaskId));
            List<TaskInfo> result = Lists.newArrayListWithCapacity(historyTasks.size());
            for (FlowHistoryTask historyTask : historyTasks) {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setId(historyTask.getId());
                taskInfo.setAssigneeId(historyTask.getAssigneeId());
                taskInfo.setExpireTime(historyTask.getExpireTime());
                taskInfo.setEndTime(historyTask.getEndTime());
                taskInfo.setDuration(historyTask.getDuration());
                if (historyTask.getState() != null) {
                    taskInfo.setState(BaseEnum.getByValue(FlowTaskState.class, historyTask.getState()));
                }
                taskInfo.setViewed(historyTask.getViewed());
                taskInfo.setActors(TaskActorInfo.of(historyTaskActorsMapping.get(historyTask.getId())));
                result.add(taskInfo);
            }
            return result;
        }
    }

    /**
     * 任务参与人信息
     */
    @Data
    static class TaskActorInfo {

        /**
         * 参与人ID
         */
        private String actorId;

        /**
         * 受理人ID
         */
        private String assigneeId;

        /**
         * 受理人类型
         */
        private AssigneeType assigneeType;

        /**
         * 评论
         */
        private String comment;

        /**
         * 附件，json格式
         */
        private String attachments;

        /**
         * 构建任务参与人信息
         *
         * @param historyTaskActors 历史任务参与人
         * @return 任务参与人信息
         */
        public static List<TaskActorInfo> of(List<FlowHistoryTaskActor> historyTaskActors) {
            if (CollectionUtil.isEmpty(historyTaskActors)) {
                return Collections.emptyList();
            }
            List<TaskActorInfo> result = Lists.newArrayListWithCapacity(historyTaskActors.size());
            for (FlowHistoryTaskActor historyTaskActor : historyTaskActors) {
                TaskActorInfo taskActorInfo = new TaskActorInfo();
                taskActorInfo.setActorId(historyTaskActor.getActorId());
                if (StrUtil.isNotBlank(historyTaskActor.getAssigneeId())) {
                    taskActorInfo.setAssigneeId(historyTaskActor.getAssigneeId());
                    taskActorInfo.setAssigneeType(AssigneeType.valueOf(historyTaskActor.getAssigneeType()));
                }
                taskActorInfo.setComment(historyTaskActor.getComment());
                taskActorInfo.setAttachments(historyTaskActor.getAttachments());
                result.add(taskActorInfo);
            }
            return result;
        }
    }
}
