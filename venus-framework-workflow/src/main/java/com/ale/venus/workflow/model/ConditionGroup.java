package com.ale.venus.workflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 条件组
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionGroup {

    /**
     * 条件
     */
    private List<Condition> conditions;
}
