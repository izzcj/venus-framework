SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for flow_definition
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_definition` (
    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
    `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
    `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
    `created_at` timestamp NOT NULL COMMENT '创建时间',
    `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '修改人',
    `updated_at` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
    `definition_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程定义 key 唯一标识',
    `business_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程定义名称',
    `pinyin_abbr` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拼音简码',
    `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '流程图标地址',
    `version` int NOT NULL DEFAULT 1 COMMENT '流程版本，默认 1',
    `use_scope` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '使用范围',
    `use_scope_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '使用范围配置',
    `published` tinyint(1) NULL DEFAULT NULL COMMENT '是否启用',
    `state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '状态',
    `design_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设计内容',
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
    `sort` int NULL DEFAULT NULL COMMENT '排序',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_definition_tenant_id`(`tenant_id` ASC) USING BTREE COMMENT '机构ID索引',
    INDEX `idx_definition_business_type`(`business_type` ASC) USING BTREE COMMENT '业务类型索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '流程定义表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for flow_execution
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_execution` (
    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
    `created_at` timestamp(6) NOT NULL COMMENT '创建时间',
    `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
    `instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程实例ID',
    `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级ID',
    `last_execution_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上次执行记录ID',
    `node_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点ID',
    `node_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '节点名称',
    `node_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点类型',
    `state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
    `completed_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
    `variable` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变量',
     PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_execution_instance_id`(`instance_id` ASC) USING BTREE COMMENT '流程实例ID索引',
    INDEX `idx_execution_node_id`(`node_id` ASC) USING BTREE COMMENT '节点ID索引',
    INDEX `idx_execution_state`(`state` ASC) USING BTREE COMMENT '状态索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '流程定义表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for flow_history_instance
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_history_instance` (
    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
    `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
    `created_at` timestamp NOT NULL COMMENT '创建时间',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
    `updated_at` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '修改人',
    `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
    `definition_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程定义ID',
    `design_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '流程设计内容',
    `business_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
    `business_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务ID',
    `current_node_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '当前节点ID',
    `current_node_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '当前节点名称',
    `expire_time` timestamp NULL DEFAULT NULL COMMENT '期望完成时间',
    `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
    `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
    `variable` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变量json',
    `state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
    `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
    `duration` bigint NULL DEFAULT NULL COMMENT '耗时',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_hi_instance_tenant_id`(`tenant_id` ASC) USING BTREE COMMENT '机构ID索引',
    INDEX `idx_hi_instance_business_type`(`business_type` ASC) USING BTREE COMMENT '业务类型索引',
    INDEX `idx_hi_instance_state`(`state` ASC) USING BTREE COMMENT '状态索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '流程实例表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for flow_history_task
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_history_task` (
    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
    `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
    `created_at` timestamp NOT NULL COMMENT '创建时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '修改人',
    `updated_at` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
    `instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程实例ID',
    `execution_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行记录ID',
    `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
    `node_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点ID',
    `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级ID',
    `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务类型',
    `owner_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属人ID',
    `assignee_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '受理人ID',
    `expire_time` timestamp NULL DEFAULT NULL COMMENT '任务期望完成时间',
    `remind_count` tinyint(1) NULL DEFAULT 0 COMMENT '提醒次数',
    `viewed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已阅',
    `variable` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变量json',
    `state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
    `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
    `duration` bigint NULL DEFAULT NULL COMMENT '耗时',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_hi_task_instance_id`(`instance_id` ASC) USING BTREE COMMENT '流程实例ID索引',
    INDEX `idx_hi_task_execution_id`(`execution_id` ASC) USING BTREE COMMENT '执行记录ID索引',
    INDEX `idx_hi_task_assignee_id`(`assignee_id` ASC) USING BTREE COMMENT '受理人ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for flow_history_task_actor
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_history_task_actor` (
    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键 ID',
    `created_at` timestamp NOT NULL COMMENT '创建时间',
    `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    `instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程实例ID',
    `execution_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行记录ID',
    `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务ID',
    `actor_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参与者ID',
    `assignee_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '受理人ID',
    `assignee_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '受理类型',
    `weight` int NULL DEFAULT NULL COMMENT '权重',
    `extend` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展json',
    `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论',
    `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '附件',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_hi_actor_task_id`(`task_id` ASC) USING BTREE COMMENT '任务ID索引',
    INDEX `idx_hi_actor_id`(`actor_id` ASC) USING BTREE COMMENT '参与人ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务参与者表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for flow_instance
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_instance` (
    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
    `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
    `created_at` timestamp NOT NULL COMMENT '创建时间',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人名称',
    `updated_at` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '修改人',
    `definition_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程定义ID',
    `design_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '流程设计内容',
    `business_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型',
    `business_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务ID',
    `current_node_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '当前节点ID',
    `current_node_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '当前节点名称',
    `expire_time` timestamp NULL DEFAULT NULL COMMENT '期望完成时间',
    `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
    `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
    `variable` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变量json',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_instance_tenant_id`(`tenant_id` ASC) USING BTREE COMMENT '机构ID索引',
    INDEX `idx_instance_business_type`(`business_type` ASC) USING BTREE COMMENT '业务类型索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '流程实例表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for flow_task
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_task` (
    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID',
    `tenant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
    `created_at` timestamp NOT NULL COMMENT '创建时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '修改人',
    `updated_at` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    `instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程实例ID',
    `execution_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行记录ID',
    `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
    `node_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点ID',
    `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级ID',
    `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务类型',
    `state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
    `owner_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属人ID',
    `assignee_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '受理人ID',
    `expire_time` timestamp NULL DEFAULT NULL COMMENT '任务期望完成时间',
    `remind_count` tinyint(1) NULL DEFAULT 0 COMMENT '提醒次数',
    `viewed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已阅',
    `variable` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变量json',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_task_instance_id`(`instance_id` ASC) USING BTREE COMMENT '流程实例ID索引',
    INDEX `idx_task_execution_id`(`execution_id` ASC) USING BTREE COMMENT '执行记录ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for flow_task_actor
-- ----------------------------
CREATE TABLE IF NOT EXISTS `flow_task_actor` (
    `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键 ID',
    `created_at` timestamp NOT NULL COMMENT '创建时间',
    `instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '流程实例ID',
    `execution_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行记录ID',
    `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务ID',
    `actor_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参与者ID',
    `assignee_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '受理人ID',
    `assignee_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '受理类型',
    `weight` int NULL DEFAULT NULL COMMENT '权重',
    `extend` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展json',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_actor_task_id`(`task_id` ASC) USING BTREE COMMENT '流程任务ID索引',
    INDEX `idx_actor_actor_id`(`actor_id` ASC) USING BTREE COMMENT '参与人ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务参与者表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
