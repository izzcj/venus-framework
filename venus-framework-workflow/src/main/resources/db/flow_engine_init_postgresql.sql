-- ----------------------------
-- flow_definition
-- ----------------------------
DO $$ BEGIN
	IF NOT EXISTS ( SELECT 1 FROM information_schema.tables WHERE table_name = 'flow_definition' ) THEN
	    CREATE TABLE "flow_definition" (
            "id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "tenant_id" VARCHAR ( 50 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_by" VARCHAR ( 50 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_at" TIMESTAMP ( 6 ) NOT NULL,
            "updated_by" VARCHAR ( 50 ) COLLATE "pg_catalog"."default",
            "updated_at" TIMESTAMP ( 6 ),
            "deleted" BOOL NOT NULL DEFAULT FALSE,
            "definition_key" VARCHAR ( 100 ) COLLATE "pg_catalog"."default" NOT NULL,
            "business_type" VARCHAR ( 32 ) COLLATE "pg_catalog"."default",
            "name" VARCHAR ( 100 ) COLLATE "pg_catalog"."default" NOT NULL,
            "pinyin_abbr" VARCHAR ( 50 ) COLLATE "pg_catalog"."default",
            "icon" VARCHAR ( 255 ) COLLATE "pg_catalog"."default",
            "version" INT2 NOT NULL,
            "use_scope" VARCHAR ( 32 ) COLLATE "pg_catalog"."default",
            "use_scope_config" TEXT COLLATE "pg_catalog"."default",
            "published" BOOL NOT NULL DEFAULT FALSE,
            "state" VARCHAR ( 32 ) COLLATE "pg_catalog"."default" NOT NULL,
            "design_content" JSONB NOT NULL,
            "description" VARCHAR ( 255 ) COLLATE "pg_catalog"."default",
            "sort" INT4
        );
        COMMENT ON COLUMN "flow_definition"."id" IS '主键ID';
        COMMENT ON COLUMN "flow_definition"."tenant_id" IS '租户ID';
        COMMENT ON COLUMN "flow_definition"."created_by" IS '创建人';
        COMMENT ON COLUMN "flow_definition"."created_at" IS '创建时间';
        COMMENT ON COLUMN "flow_definition"."updated_by" IS '修改人';
        COMMENT ON COLUMN "flow_definition"."updated_at" IS '修改时间';
        COMMENT ON COLUMN "flow_definition"."deleted" IS '是否删除';
        COMMENT ON COLUMN "flow_definition"."definition_key" IS '流程定义 key 唯一标识';
        COMMENT ON COLUMN "flow_definition"."business_type" IS '业务类型';
        COMMENT ON COLUMN "flow_definition"."name" IS '流程定义名称';
        COMMENT ON COLUMN "flow_definition"."pinyin_abbr" IS '拼音简码';
        COMMENT ON COLUMN "flow_definition"."icon" IS '流程图标地址';
        COMMENT ON COLUMN "flow_definition"."version" IS '流程版本，默认 1';
        COMMENT ON COLUMN "flow_definition"."use_scope" IS '使用范围';
        COMMENT ON COLUMN "flow_definition"."use_scope_config" IS '使用范围配置';
        COMMENT ON COLUMN "flow_definition"."published" IS '是否启用';
        COMMENT ON COLUMN "flow_definition"."state" IS '状态';
        COMMENT ON COLUMN "flow_definition"."design_content" IS '设计内容';
        COMMENT ON COLUMN "flow_definition"."description" IS '描述';
        COMMENT ON COLUMN "flow_definition"."sort" IS '排序';
        COMMENT ON TABLE "flow_definition" IS '流程定义表';
        CREATE INDEX "idx_definition_business_type" ON "flow_definition" USING btree ( "business_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST );
        COMMENT ON INDEX "idx_definition_business_type" IS '业务类型索引';
        CREATE INDEX "idx_definition_tenant_id" ON "flow_definition" USING btree ( "tenant_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST );
        COMMENT ON INDEX "idx_definition_tenant_id" IS '机构ID索引';
        ALTER TABLE "flow_definition" ADD CONSTRAINT "flow_definition_pkey" PRIMARY KEY ( "id" );
    END IF;
END $$;
-- ----------------------------
-- flow_execution
-- ----------------------------
DO $$ BEGIN
	IF NOT EXISTS ( SELECT 1 FROM information_schema.tables WHERE TABLE_NAME = 'flow_execution' ) THEN
        CREATE TABLE "flow_execution" (
            "id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_at" TIMESTAMP ( 6 ) NOT NULL,
            "deleted" BOOL NOT NULL DEFAULT FALSE,
            "instance_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "parent_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "last_execution_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "node_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "node_name" VARCHAR ( 128 ) COLLATE "pg_catalog"."default",
            "node_type" VARCHAR ( 32 ) COLLATE "pg_catalog"."default" NOT NULL,
            "state" VARCHAR ( 32 ) COLLATE "pg_catalog"."default" NOT NULL,
            "completed_time" TIMESTAMP ( 6 ),
            "variable" JSONB
        );
        COMMENT ON COLUMN "flow_execution"."id" IS '主键ID';
        COMMENT ON COLUMN "flow_execution"."created_at" IS '创建时间';
        COMMENT ON COLUMN "flow_execution"."deleted" IS '是否删除';
        COMMENT ON COLUMN "flow_execution"."instance_id" IS '流程实例ID';
        COMMENT ON COLUMN "flow_execution"."parent_id" IS '父级ID';
        COMMENT ON COLUMN "flow_execution"."last_execution_id" IS '上次执行记录ID';
        COMMENT ON COLUMN "flow_execution"."node_id" IS '节点ID';
        COMMENT ON COLUMN "flow_execution"."node_name" IS '节点名称';
        COMMENT ON COLUMN "flow_execution"."node_type" IS '节点类型';
        COMMENT ON COLUMN "flow_execution"."state" IS '状态';
        COMMENT ON COLUMN "flow_execution"."completed_time" IS '完成时间';
        COMMENT ON COLUMN "flow_execution"."variable" IS '变量';
        COMMENT ON TABLE "flow_execution" IS '流程定义表';
        CREATE INDEX "idx_execution_instance_id" ON "flow_execution" USING btree ( "instance_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST );
        COMMENT ON INDEX "idx_execution_instance_id" IS '流程实例ID索引';
        CREATE INDEX "idx_execution_node_id" ON "flow_execution" USING btree ( "node_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST );
        COMMENT ON INDEX "idx_execution_node_id" IS '节点ID索引';
        CREATE INDEX "idx_execution_state" ON "flow_execution" USING btree ( "state" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST );
        COMMENT ON INDEX "idx_execution_state" IS '状态索引';
        ALTER TABLE "flow_execution" ADD CONSTRAINT "flow_execution_pkey" PRIMARY KEY ( "id" );
    END IF;
END $$;
-- ----------------------------
-- flow_history_instance
-- ----------------------------
DO $$ BEGIN
    IF NOT EXISTS ( SELECT 1 FROM information_schema.tables WHERE TABLE_NAME = 'flow_history_instance' ) THEN
        CREATE TABLE "flow_history_instance" (
            "id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "tenant_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_at" TIMESTAMP ( 6 ) NOT NULL,
            "created_by" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "updated_at" TIMESTAMP ( 6 ),
            "updated_by" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "deleted" BOOL NOT NULL DEFAULT FALSE,
            "definition_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "design_content" JSONB,
            "business_type" VARCHAR ( 32 ) COLLATE "pg_catalog"."default",
            "business_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "current_node_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "current_node_name" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "expire_time" TIMESTAMP ( 6 ),
            "title" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "description" VARCHAR ( 512 ) COLLATE "pg_catalog"."default",
            "variable" JSONB,
            "state" VARCHAR ( 32 ) COLLATE "pg_catalog"."default" NOT NULL,
            "end_time" TIMESTAMP ( 6 ),
            "duration" INT8
        );
        COMMENT ON TABLE "flow_history_instance" IS '流程实例表';
        COMMENT ON COLUMN "flow_history_instance"."id" IS '主键ID';
        COMMENT ON COLUMN "flow_history_instance"."tenant_id" IS '租户ID';
        COMMENT ON COLUMN "flow_history_instance"."created_at" IS '创建时间';
        COMMENT ON COLUMN "flow_history_instance"."created_by" IS '创建人名称';
        COMMENT ON COLUMN "flow_history_instance"."updated_at" IS '修改时间';
        COMMENT ON COLUMN "flow_history_instance"."updated_by" IS '修改人';
        COMMENT ON COLUMN "flow_history_instance"."deleted" IS '是否删除';
        COMMENT ON COLUMN "flow_history_instance"."definition_id" IS '流程定义ID';
        COMMENT ON COLUMN "flow_history_instance"."design_content" IS '流程设计内容';
        COMMENT ON COLUMN "flow_history_instance"."business_type" IS '业务类型';
        COMMENT ON COLUMN "flow_history_instance"."business_id" IS '业务ID';
        COMMENT ON COLUMN "flow_history_instance"."current_node_id" IS '当前节点ID';
        COMMENT ON COLUMN "flow_history_instance"."current_node_name" IS '当前节点名称';
        COMMENT ON COLUMN "flow_history_instance"."expire_time" IS '期望完成时间';
        COMMENT ON COLUMN "flow_history_instance"."title" IS '标题';
        COMMENT ON COLUMN "flow_history_instance"."description" IS '描述';
        COMMENT ON COLUMN "flow_history_instance"."variable" IS '变量json';
        COMMENT ON COLUMN "flow_history_instance"."state" IS '状态';
        COMMENT ON COLUMN "flow_history_instance"."end_time" IS '结束时间';
        COMMENT ON COLUMN "flow_history_instance"."duration" IS '耗时';
        CREATE INDEX "idx_hi_instance_business_type" ON "flow_history_instance" USING btree ("business_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST);
        COMMENT ON INDEX "idx_hi_instance_business_type" IS '业务类型索引';
        CREATE INDEX "idx_hi_instance_state" ON "flow_history_instance" USING btree ("state" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST);
        COMMENT ON INDEX "idx_hi_instance_state" IS '状态索引';
        CREATE INDEX "idx_hi_instance_tenant_id" ON "flow_history_instance" USING btree ("tenant_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST);
        COMMENT ON INDEX "idx_hi_instance_tenant_id" IS '机构ID索引';
        ALTER TABLE "flow_history_instance" ADD CONSTRAINT "flow_history_instance_pkey" PRIMARY KEY ( "id" );
    END IF;
END $$;
-- ----------------------------
-- flow_history_task
-- ----------------------------
DO $$ BEGIN
	IF NOT EXISTS ( SELECT 1 FROM information_schema.tables WHERE TABLE_NAME = 'flow_history_task' ) THEN
        CREATE TABLE "flow_history_task" (
            "id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "tenant_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_by" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_at" TIMESTAMP ( 6 ) NOT NULL,
            "updated_by" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "updated_at" TIMESTAMP ( 6 ),
            "deleted" BOOL NOT NULL DEFAULT FALSE,
            "instance_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "execution_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "name" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "node_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "parent_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "type" VARCHAR ( 32 ) COLLATE "pg_catalog"."default" NOT NULL,
            "owner_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "assignee_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "expire_time" TIMESTAMP ( 6 ),
            "remind_count" INT2,
            "viewed" BOOL NOT NULL,
            "variable" JSONB,
            "state" VARCHAR ( 32 ) COLLATE "pg_catalog"."default" NOT NULL,
            "end_time" TIMESTAMP ( 6 ),
            "duration" INT8
        );
        COMMENT ON TABLE "flow_history_task" IS '任务表';
        COMMENT ON COLUMN "flow_history_task"."id" IS '主键ID';
        COMMENT ON COLUMN "flow_history_task"."tenant_id" IS '租户ID';
        COMMENT ON COLUMN "flow_history_task"."created_by" IS '创建人';
        COMMENT ON COLUMN "flow_history_task"."created_at" IS '创建时间';
        COMMENT ON COLUMN "flow_history_task"."updated_by" IS '修改人';
        COMMENT ON COLUMN "flow_history_task"."updated_at" IS '修改时间';
        COMMENT ON COLUMN "flow_history_task"."deleted" IS '是否删除';
        COMMENT ON COLUMN "flow_history_task"."instance_id" IS '流程实例ID';
        COMMENT ON COLUMN "flow_history_task"."execution_id" IS '执行记录ID';
        COMMENT ON COLUMN "flow_history_task"."name" IS '任务名称';
        COMMENT ON COLUMN "flow_history_task"."node_id" IS '节点ID';
        COMMENT ON COLUMN "flow_history_task"."parent_id" IS '父级ID';
        COMMENT ON COLUMN "flow_history_task"."type" IS '任务类型';
        COMMENT ON COLUMN "flow_history_task"."owner_id" IS '所属人ID';
        COMMENT ON COLUMN "flow_history_task"."assignee_id" IS '受理人ID';
        COMMENT ON COLUMN "flow_history_task"."expire_time" IS '任务期望完成时间';
        COMMENT ON COLUMN "flow_history_task"."remind_count" IS '提醒次数';
        COMMENT ON COLUMN "flow_history_task"."viewed" IS '是否已阅';
        COMMENT ON COLUMN "flow_history_task"."variable" IS '变量json';
        COMMENT ON COLUMN "flow_history_task"."state" IS '状态';
        COMMENT ON COLUMN "flow_history_task"."end_time" IS '结束时间';
        COMMENT ON COLUMN "flow_history_task"."duration" IS '耗时';
        CREATE INDEX "idx_hi_task_assignee_id" ON "flow_history_task" USING btree ("assignee_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST);
        COMMENT ON INDEX "idx_hi_task_assignee_id" IS '受理人ID索引';
        CREATE INDEX "idx_hi_task_execution_id" ON "flow_history_task" USING btree ("execution_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST);
        COMMENT ON INDEX "idx_hi_task_execution_id" IS '执行记录ID索引';
        CREATE INDEX "idx_hi_task_instance_id" ON "flow_history_task" USING btree ("instance_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST);
        COMMENT ON INDEX "idx_hi_task_instance_id" IS '流程实例ID索引';
        ALTER TABLE "flow_history_task" ADD CONSTRAINT "flow_history_task_pkey" PRIMARY KEY ( "id" );
    END IF;
END $$;
-- ----------------------------
-- flow_history_task_actor
-- ----------------------------
DO $$ BEGIN
	IF NOT EXISTS ( SELECT 1 FROM information_schema.tables WHERE TABLE_NAME = 'flow_history_task_actor' ) THEN
        CREATE TABLE "flow_history_task_actor" (
            "id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_at" TIMESTAMP ( 6 ) NOT NULL,
            "deleted" INT2 NOT NULL,
            "instance_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "execution_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "task_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "actor_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "assignee_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "assignee_type" VARCHAR ( 32 ) COLLATE "pg_catalog"."default",
            "weight" INT2,
            "extend" JSONB,
            "comment" VARCHAR ( 255 ) COLLATE "pg_catalog"."default",
            "attachments" JSONB
        );
        COMMENT ON TABLE "flow_history_task_actor" IS '任务参与者表';
        COMMENT ON COLUMN "flow_history_task_actor"."id" IS '主键 ID';
        COMMENT ON COLUMN "flow_history_task_actor"."created_at" IS '创建时间';
        COMMENT ON COLUMN "flow_history_task_actor"."deleted" IS '是否删除';
        COMMENT ON COLUMN "flow_history_task_actor"."instance_id" IS '流程实例ID';
        COMMENT ON COLUMN "flow_history_task_actor"."execution_id" IS '执行记录ID';
        COMMENT ON COLUMN "flow_history_task_actor"."task_id" IS '任务ID';
        COMMENT ON COLUMN "flow_history_task_actor"."actor_id" IS '参与者ID';
        COMMENT ON COLUMN "flow_history_task_actor"."assignee_id" IS '受理人ID';
        COMMENT ON COLUMN "flow_history_task_actor"."assignee_type" IS '受理类型';
        COMMENT ON COLUMN "flow_history_task_actor"."weight" IS '权重';
        COMMENT ON COLUMN "flow_history_task_actor"."extend" IS '扩展json';
        COMMENT ON COLUMN "flow_history_task_actor"."comment" IS '评论';
        COMMENT ON COLUMN "flow_history_task_actor"."attachments" IS '附件';
        CREATE INDEX "idx_hi_actor_id" ON "flow_history_task_actor" USING btree ("actor_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST);
        COMMENT ON INDEX "idx_hi_actor_id" IS '参与人ID索引';
        CREATE INDEX "idx_hi_actor_task_id" ON "flow_history_task_actor" USING btree ("task_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST);
        COMMENT ON INDEX "idx_hi_actor_task_id" IS '任务ID索引';
        ALTER TABLE "flow_history_task_actor" ADD CONSTRAINT "flow_history_task_actor_pkey" PRIMARY KEY ( "id" );
    END IF;
END $$;
-- ----------------------------
-- flow_instance
-- ----------------------------
DO $$ BEGIN
	IF NOT EXISTS ( SELECT 1 FROM information_schema.tables WHERE TABLE_NAME = 'flow_instance' ) THEN
        CREATE TABLE "flow_instance" (
            "id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "tenant_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_at" TIMESTAMP ( 6 ) NOT NULL,
            "created_by" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "updated_at" TIMESTAMP ( 6 ),
            "updated_by" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "definition_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "design_content" JSONB,
            "business_type" VARCHAR ( 32 ) COLLATE "pg_catalog"."default",
            "business_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "current_node_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "current_node_name" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "expire_time" TIMESTAMP ( 6 ),
            "title" VARCHAR ( 128 ) COLLATE "pg_catalog"."default",
            "description" VARCHAR ( 512 ) COLLATE "pg_catalog"."default",
            "variable" JSONB
        );
        COMMENT ON COLUMN "flow_instance"."id" IS '主键ID';
        COMMENT ON COLUMN "flow_instance"."tenant_id" IS '租户ID';
        COMMENT ON COLUMN "flow_instance"."created_at" IS '创建时间';
        COMMENT ON COLUMN "flow_instance"."created_by" IS '创建人名称';
        COMMENT ON COLUMN "flow_instance"."updated_at" IS '修改时间';
        COMMENT ON COLUMN "flow_instance"."updated_by" IS '修改人';
        COMMENT ON COLUMN "flow_instance"."definition_id" IS '流程定义ID';
        COMMENT ON COLUMN "flow_instance"."design_content" IS '流程设计内容';
        COMMENT ON COLUMN "flow_instance"."business_type" IS '业务类型';
        COMMENT ON COLUMN "flow_instance"."business_id" IS '业务ID';
        COMMENT ON COLUMN "flow_instance"."current_node_id" IS '当前节点ID';
        COMMENT ON COLUMN "flow_instance"."current_node_name" IS '当前节点名称';
        COMMENT ON COLUMN "flow_instance"."expire_time" IS '期望完成时间';
        COMMENT ON COLUMN "flow_instance"."title" IS '标题';
        COMMENT ON COLUMN "flow_instance"."description" IS '描述';
        COMMENT ON COLUMN "flow_instance"."variable" IS '变量json';
        COMMENT ON TABLE "flow_instance" IS '流程实例表';
        CREATE INDEX "idx_instance_business_type" ON "flow_instance" USING btree ( "business_type" COLLATE "pg_catalog"."default" ASC NULLS LAST );
        COMMENT ON INDEX "idx_instance_business_type" IS '业务类型索引';
        CREATE INDEX "idx_instance_tenant_id" ON "flow_instance" USING btree ( "tenant_id" COLLATE "pg_catalog"."default" ASC NULLS LAST );
        COMMENT ON INDEX "idx_instance_tenant_id" IS '机构ID索引';
        ALTER TABLE "flow_instance" ADD CONSTRAINT "flow_instance_pkey" PRIMARY KEY ( "id" );
    END IF;
END $$;
-- ----------------------------
-- flow_task
-- ----------------------------
DO $$ BEGIN
	IF NOT EXISTS ( SELECT 1 FROM information_schema.tables WHERE TABLE_NAME = 'flow_task' ) THEN
        CREATE TABLE "flow_task" (
            "id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "tenant_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_by" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_at" TIMESTAMP ( 6 ) NOT NULL,
            "updated_by" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "updated_at" TIMESTAMP ( 6 ),
            "instance_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "execution_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "name" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "node_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "parent_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "type" VARCHAR ( 32 ) COLLATE "pg_catalog"."default" NOT NULL,
            "state" VARCHAR ( 32 ) COLLATE "pg_catalog"."default" NOT NULL,
            "owner_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "assignee_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "expire_time" TIMESTAMP ( 6 ),
            "remind_count" INT2,
            "viewed" BOOL NOT NULL,
            "variable" JSONB
        );
        COMMENT ON COLUMN "flow_task"."id" IS '主键ID';
        COMMENT ON COLUMN "flow_task"."tenant_id" IS '租户ID';
        COMMENT ON COLUMN "flow_task"."created_by" IS '创建人';
        COMMENT ON COLUMN "flow_task"."created_at" IS '创建时间';
        COMMENT ON COLUMN "flow_task"."updated_by" IS '修改人';
        COMMENT ON COLUMN "flow_task"."updated_at" IS '修改时间';
        COMMENT ON COLUMN "flow_task"."instance_id" IS '流程实例ID';
        COMMENT ON COLUMN "flow_task"."execution_id" IS '执行记录ID';
        COMMENT ON COLUMN "flow_task"."name" IS '任务名称';
        COMMENT ON COLUMN "flow_task"."node_id" IS '节点ID';
        COMMENT ON COLUMN "flow_task"."parent_id" IS '父级ID';
        COMMENT ON COLUMN "flow_task"."type" IS '任务类型';
        COMMENT ON COLUMN "flow_task"."state" IS '状态';
        COMMENT ON COLUMN "flow_task"."owner_id" IS '所属人ID';
        COMMENT ON COLUMN "flow_task"."assignee_id" IS '受理人ID';
        COMMENT ON COLUMN "flow_task"."expire_time" IS '期望完成时间';
        COMMENT ON COLUMN "flow_task"."remind_count" IS '提醒次数';
        COMMENT ON COLUMN "flow_task"."viewed" IS '是否已阅';
        COMMENT ON COLUMN "flow_task"."variable" IS '变量json';
        COMMENT ON TABLE "flow_task" IS '任务表';
        CREATE INDEX "idx_task_instance_id" ON "flow_task" USING btree ( "instance_id" COLLATE "pg_catalog"."default" ASC NULLS LAST );
        COMMENT ON INDEX "idx_task_instance_id" IS '流程实例ID索引';
        CREATE INDEX "idx_task_node_id" ON "flow_task" USING btree ( "node_id" COLLATE "pg_catalog"."default" ASC NULLS LAST );
        COMMENT ON INDEX "idx_task_node_id" IS '节点ID索引';
        CREATE INDEX "idx_task_state" ON "flow_task" USING btree ( "state" COLLATE "pg_catalog"."default" ASC NULLS LAST );
        COMMENT ON INDEX "idx_task_state" IS '任务状态索引';
        ALTER TABLE "flow_task" ADD CONSTRAINT "flow_task_pkey" PRIMARY KEY ( "id" );
    END IF;
END $$;
-- ----------------------------
-- flow_task_actor
-- ----------------------------
DO $$ BEGIN
	IF NOT EXISTS ( SELECT 1 FROM information_schema.tables WHERE TABLE_NAME = 'flow_task_actor' ) THEN
        CREATE TABLE "flow_task_actor" (
            "id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "created_at" TIMESTAMP ( 6 ) NOT NULL,
            "instance_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "execution_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "task_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "actor_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default" NOT NULL,
            "assignee_id" VARCHAR ( 64 ) COLLATE "pg_catalog"."default",
            "assignee_type" VARCHAR ( 32 ) COLLATE "pg_catalog"."default",
            "weight" INT2,
            "extend" JSONB
        );
        COMMENT ON COLUMN "flow_task_actor"."id" IS '主键 ID';
        COMMENT ON COLUMN "flow_task_actor"."created_at" IS '创建时间';
        COMMENT ON COLUMN "flow_task_actor"."instance_id" IS '流程实例ID';
        COMMENT ON COLUMN "flow_task_actor"."execution_id" IS '执行记录ID';
        COMMENT ON COLUMN "flow_task_actor"."task_id" IS '任务ID';
        COMMENT ON COLUMN "flow_task_actor"."actor_id" IS '参与者ID';
        COMMENT ON COLUMN "flow_task_actor"."assignee_id" IS '受理人ID';
        COMMENT ON COLUMN "flow_task_actor"."assignee_type" IS '受理类型';
        COMMENT ON COLUMN "flow_task_actor"."weight" IS '权重';
        COMMENT ON COLUMN "flow_task_actor"."extend" IS '扩展json';
        COMMENT ON TABLE "flow_task_actor" IS '任务参与者表';
        CREATE INDEX "idx_actor_actor_id" ON "flow_task_actor" USING btree ( "actor_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST );
        COMMENT ON INDEX "idx_actor_actor_id" IS '参与人ID索引';
        CREATE INDEX "idx_actor_task_id" ON "flow_task_actor" USING btree ( "task_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST );
        COMMENT ON INDEX "idx_actor_task_id" IS '流程任务ID索引';
        ALTER TABLE "flow_task_actor" ADD CONSTRAINT "flow_task_actor_pkey" PRIMARY KEY ( "id" );
    END IF;
END $$;
