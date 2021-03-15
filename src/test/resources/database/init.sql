create table tcms_material_job_log
(
    id          varchar(40)  not null
        primary key,
    type        varchar(255) null comment '产品，物料，Bom，模型号',
    start_time  bigint       null comment '执行时间',
    excute_type int          null comment '执行方式 0自动 1手动',
    ent_time    bigint       null comment '结束时间',
    status      int          null comment '执行状态 0执行中 1已完成 2错误',
    progress    int          null comment '执行进度'
);

