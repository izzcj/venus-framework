package com.ale.venus.workflow.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.workflow.dao.FlowDefinitionDao;
import com.ale.venus.workflow.entity.FlowDefinition;
import com.ale.venus.workflow.enumeration.FlowDefinitionState;
import com.ale.venus.workflow.exception.FlowException;
import com.ale.venus.workflow.service.DefinitionService;
import com.ale.venus.workflow.support.IdGeneratorSupport;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认流程定义服务
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class DefaultDefinitionService implements DefinitionService {

    /**
     * 流程定义数据访问对象
     */
    private final FlowDefinitionDao flowDefinitionDao;

    public DefaultDefinitionService(FlowDefinitionDao flowDefinitionDao) {
        this.flowDefinitionDao = flowDefinitionDao;
    }

    @Override
    public String deploy(FlowDefinition flowDefinition, boolean override) {
        String key = flowDefinition.getDefinitionKey();
        if (StrUtil.isBlank(key)) {
            key = IdUtil.fastUUID();
        }
        FlowDefinition oldDefinition = this.flowDefinitionDao.selectByKey(key, flowDefinition.getTenantId());
        if (oldDefinition != null) {
            if (override) {
                boolean updateResult = this.flowDefinitionDao.updateByKey(flowDefinition, key, flowDefinition.getTenantId());
                if (updateResult) {
                    return oldDefinition.getId();
                }
                throw new FlowException("流程定义[{}]部署失败！", flowDefinition.getName());
            }
            flowDefinition.setId(IdGeneratorSupport.generateId());
            // 否则版本+1
            flowDefinition.setVersion(oldDefinition.nextVersion());
            if (this.flowDefinitionDao.insert(flowDefinition)) {
                return flowDefinition.getId();
            }
            throw new FlowException("流程定义[{}]部署失败！", flowDefinition.getName());
        }
        flowDefinition.setId(IdGeneratorSupport.generateId());
        flowDefinition.setDefinitionKey(key);
        flowDefinition.setVersion(1);
        flowDefinition.setDeleted(false);
        if (this.flowDefinitionDao.insert(flowDefinition)) {
            return flowDefinition.getId();
        }
        throw new FlowException("流程定义[{}]部署失败！", flowDefinition.getName());
    }

    @Override
    public boolean update(FlowDefinition flowDefinition) {
        this.checkFlowDefinitionIsExist("更新流程定义", flowDefinition.getId());
        return this.flowDefinitionDao.updateById(flowDefinition);
    }

    @Override
    public boolean undeploy(String id) {
        this.checkFlowDefinitionIsExist("卸载流程定义", id);
        FlowDefinition flowDefinition = this.flowDefinitionDao.selectById(id);
        flowDefinition.setState(FlowDefinitionState.SUSPENDED.getValue());
        return this.flowDefinitionDao.updateById(flowDefinition);
    }

    @Override
    public String remove(String id) {
        this.checkFlowDefinitionIsExist("删除流程定义", id);
        if (this.flowDefinitionDao.deleteById(id)) {
            return id;
        }
        throw new FlowException("删除流程定义[{}]失败！", id);
    }

    /**
     * 检查流程定义是否存在
     *
     * @param id   流程定义ID
     * @param tips 提示信息
     */
    private void checkFlowDefinitionIsExist(String id, String tips) {
        if (StrUtil.isBlank(id)) {
            throw new FlowException(tips + "失败！流程定义ID为空！");
        }
        if (this.flowDefinitionDao.notExist(id)) {
            throw new FlowException(tips + "失败！流程定义[{}]不存在！流程定义ID：", id);
        }
    }
}
