package com.ale.venus.workflow.executor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.workflow.model.ConditionGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring el条件执行器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class SpelConditionExecutor implements ConditionExecutor {

    /**
     * 表达式解析器
     */
    private final ExpressionParser parser;

    /**
     * 条件表达式构建器
     */
    private final ExpressionBuilder expressionBuilder;

    public SpelConditionExecutor(ExpressionBuilder expressionBuilder) {
        this.parser = new SpelExpressionParser();
        this.expressionBuilder = expressionBuilder;
    }

    @Override
    public boolean execute(List<ConditionGroup> conditionGroups, Map<String, Object> args) {
        if (CollectionUtil.isEmpty(conditionGroups)) {
            return true;
        }
        String expression = conditionGroups.stream()
            .map(ConditionGroup::getConditions)
            .filter(CollectionUtil::isNotEmpty)
            .map(conditions -> conditions.stream()
                .map(this.expressionBuilder::build)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining(" && "))
            )
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.joining(" || "));
        if (StrUtil.isBlank(expression)) {
            log.error("条件组[{}]无法构建执行表达式！", conditionGroups);
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug("条件组[{}]执行，表达式：{}", conditionGroups, expression);
        }
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(args);
        return Boolean.TRUE.equals(this.parser.parseExpression(expression).getValue(context, Boolean.class));
    }
}
