# 1. 创建数据库
drop database if exists athena_iam;

create database athena_iam default character set utf8mb4 collate utf8mb4_unicode_ci;

use athena_iam;

set names utf8mb4;

# 2. 租户表 - 优化版
drop table if exists sys_tenant;
create table sys_tenant
(
    id                 bigint                                            not null auto_increment comment '租户id',
    tenant_code        varchar(64)                                       not null comment '租户编码',
    tenant_name        varchar(128)                                      not null comment '租户名称',
    tenant_type        enum ('enterprise','personal','trial')            not null default 'enterprise' comment '租户类型',
    tenant_level       enum ('basic','standard','enterprise','flagship') not null default 'basic' comment '租户等级',
    domain             varchar(128)                                               default null comment '租户域名',
    logo               varchar(512)                                               default null comment '租户logo url',
    contact_name       varchar(64)                                                default null comment '联系人姓名',
    contact_phone      varchar(32)                                                default null comment '联系人电话',
    contact_email      varchar(128)                                               default null comment '联系人邮箱',
    address            varchar(255)                                               default null comment '联系地址',
    license_key        varchar(512)                                               default null comment '授权密钥',
    package_id         bigint                                                     default null comment '当前套餐id',
    expire_time        timestamp                                         null     default null comment '到期时间',
    max_user_count     int                                               not null default 100 comment '最大用户数(-1表示无限制)',
    current_user_count int                                               not null default 0 comment '当前用户数',
    storage_quota      bigint                                            not null default 1073741824 comment '存储配额(字节,-1表示无限制)',
    used_storage       bigint                                            not null default 0 comment '已用存储(字节)',
    status             enum ('active','inactive','expired','suspended')  not null default 'active' comment '状态',
    create_by          bigint                                                     default null comment '创建人',
    create_time        timestamp                                         not null default current_timestamp comment '创建时间',
    update_by          bigint                                                     default null comment '更新人',
    update_time        timestamp                                         not null default current_timestamp on update current_timestamp comment '更新时间',
    delete_flag        tinyint(1)                                        not null default 0 comment '删除标志 0-正常 1-删除',
    version            int                                               not null default 1 comment '乐观锁版本号',
    remark             varchar(500)                                               default null comment '备注',
    primary key (id),
    unique key uk_tenant_code (tenant_code),
    unique key uk_domain (domain),
    key idx_tenant_type_status (tenant_type, status),
    key idx_expire_time (expire_time),
    key idx_package_id (package_id),
    key idx_create_time (create_time),
    key idx_delete_flag (delete_flag)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='租户表';

# 3. 租户配置表 - 优化版
drop table if exists sys_tenant_config;
create table sys_tenant_config
(
    id           bigint                                                not null auto_increment comment '配置id',
    tenant_id    bigint                                                not null comment '租户id',
    config_key   varchar(128)                                          not null comment '配置键',
    config_value longtext comment '配置值',
    config_type  enum ('string','number','boolean','json','encrypted') not null default 'string' comment '配置类型',
    is_sensitive tinyint(1)                                            not null default 0 comment '是否敏感配置 0-否 1-是',
    create_by    bigint                                                         default null comment '创建人',
    create_time  timestamp                                             not null default current_timestamp comment '创建时间',
    update_by    bigint                                                         default null comment '更新人',
    update_time  timestamp                                             not null default current_timestamp on update current_timestamp comment '更新时间',
    version      int                                                   not null default 1 comment '乐观锁版本号',
    remark       varchar(500)                                                   default null comment '备注',
    primary key (id),
    unique key uk_tenant_config (tenant_id, config_key),
    key idx_config_type (config_type),
    constraint fk_tenant_config_tenant foreign key (tenant_id) references sys_tenant (id) on delete cascade
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='租户配置表';

# 4. 租户套餐表 - 优化版
drop table if exists sys_tenant_package;
create table sys_tenant_package
(
    id             bigint                       not null auto_increment comment '套餐id',
    package_code   varchar(64)                  not null comment '套餐编码',
    package_name   varchar(128)                 not null comment '套餐名称',
    package_type   enum ('free','paid','trial') not null default 'free' comment '套餐类型',
    max_user_count int                          not null default 10 comment '最大用户数(-1表示无限制)',
    storage_quota  bigint                       not null default 1073741824 comment '存储配额(字节,-1表示无限制)',
    max_app_count  int                          not null default 5 comment '最大应用数(-1表示无限制)',
    feature_list   json                                  default null comment '功能列表(json格式)',
    price_monthly  decimal(10, 2)               not null default 0.00 comment '月价格(元)',
    price_yearly   decimal(10, 2)               not null default 0.00 comment '年价格(元)',
    trial_days     int                          not null default 0 comment '试用天数',
    sort_order     int                          not null default 0 comment '排序',
    status         enum ('active','inactive')   not null default 'active' comment '状态',
    create_by      bigint                                default null comment '创建人',
    create_time    timestamp                    not null default current_timestamp comment '创建时间',
    update_by      bigint                                default null comment '更新人',
    update_time    timestamp                    not null default current_timestamp on update current_timestamp comment '更新时间',
    version        int                          not null default 1 comment '乐观锁版本号',
    remark         varchar(500)                          default null comment '备注',
    primary key (id),
    unique key uk_package_code (package_code),
    key idx_package_type_status (package_type, status),
    key idx_sort_order (sort_order)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='租户套餐表';

# 5. 系统应用表 - 优化版
drop table if exists sys_application;
create table sys_application
(
    id                     bigint                                               not null auto_increment comment '系统id',
    tenant_id              bigint                                               not null default 0 comment '租户id(0表示平台系统)',
    app_code               varchar(64)                                          not null comment '系统编码',
    app_name               varchar(128)                                         not null comment '系统名称',
    app_type               enum ('web','mobile','desktop','api','microservice') not null default 'web' comment '系统类型',
    app_version            varchar(32)                                          not null default '1.0.0' comment '系统版本',
    app_url                varchar(512)                                                  default null comment '系统访问地址',
    app_icon               varchar(512)                                                  default null comment '系统图标url',
    app_description        text comment '系统描述',
    client_id              varchar(128)                                                  default null comment '客户端id(oauth2)',
    client_secret          varchar(512)                                                  default null comment '客户端密钥(oauth2,加密存储)',
    redirect_uris          json                                                          default null comment '回调地址列表(json格式)',
    grant_types            varchar(255)                                         not null default 'authorization_code,refresh_token' comment '授权类型',
    scopes                 varchar(255)                                         not null default 'read,write' comment '授权范围',
    access_token_validity  int                                                  not null default 3600 comment 'accesstoken有效期(秒)',
    refresh_token_validity int                                                  not null default 86400 comment 'refreshtoken有效期(秒)',
    auto_approve           tinyint(1)                                           not null default 0 comment '是否自动授权',
    is_system              tinyint(1)                                           not null default 0 comment '是否系统应用',
    sort_order             int                                                  not null default 0 comment '排序',
    status                 enum ('active','inactive','maintenance')             not null default 'active' comment '状态',
    create_by              bigint                                                        default null comment '创建人',
    create_time            timestamp                                            not null default current_timestamp comment '创建时间',
    update_by              bigint                                                        default null comment '更新人',
    update_time            timestamp                                            not null default current_timestamp on update current_timestamp comment '更新时间',
    delete_flag            tinyint(1)                                           not null default 0 comment '删除标志',
    version                int                                                  not null default 1 comment '乐观锁版本号',
    remark                 varchar(500)                                                  default null comment '备注',
    primary key (id),
    unique key uk_tenant_app_code (tenant_id, app_code),
    unique key uk_client_id (client_id),
    key idx_tenant_type_status (tenant_id, app_type, status),
    key idx_sort_order (sort_order),
    key idx_delete_flag (delete_flag),
    constraint fk_application_tenant foreign key (tenant_id) references sys_tenant (id) on delete cascade
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='系统应用表';

# 6. 系统配置表 - 优化版
drop table if exists sys_app_config;
create table sys_app_config
(
    id           bigint                                                not null auto_increment comment '配置id',
    tenant_id    bigint                                                not null default 0 comment '租户id',
    app_id       bigint                                                not null comment '系统id',
    config_key   varchar(128)                                          not null comment '配置键',
    config_value longtext comment '配置值',
    config_type  enum ('string','number','boolean','json','encrypted') not null default 'string' comment '配置类型',
    is_sensitive tinyint(1)                                            not null default 0 comment '是否敏感配置',
    create_by    bigint                                                         default null comment '创建人',
    create_time  timestamp                                             not null default current_timestamp comment '创建时间',
    update_by    bigint                                                         default null comment '更新人',
    update_time  timestamp                                             not null default current_timestamp on update current_timestamp comment '更新时间',
    version      int                                                   not null default 1 comment '乐观锁版本号',
    remark       varchar(500)                                                   default null comment '备注',
    primary key (id),
    unique key uk_app_config (app_id, config_key),
    key idx_tenant_id (tenant_id),
    key idx_config_type (config_type),
    constraint fk_app_config_application foreign key (app_id) references sys_application (id) on delete cascade
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='系统配置表';

# 7. 用户表 - 优化版
drop table if exists sys_user;
create table sys_user
(
    id                      bigint                                        not null auto_increment comment '用户id',
    tenant_id               bigint                                        not null default 0 comment '租户id(0表示平台管理员)',
    username                varchar(64)                                   not null comment '用户名',
    password                varchar(128)                                  not null comment '密码(bcrypt加密)',
    nick_name               varchar(64)                                            default null comment '昵称',
    real_name               varchar(64)                                            default null comment '真实姓名',
    email                   varchar(128)                                           default null comment '邮箱',
    phone                   varchar(32)                                            default null comment '手机号',
    avatar                  varchar(512)                                           default null comment '头像url',
    gender                  enum ('unknown','male','female')              not null default 'unknown' comment '性别',
    birthday                date                                                   default null comment '生日',
    dept_id                 bigint                                                 default null comment '部门id',
    is_tenant_admin         tinyint(1)                                    not null default 0 comment '是否租户管理员',
    default_app_id          bigint                                                 default null comment '默认系统id',
    status                  enum ('active','inactive','locked','expired') not null default 'active' comment '状态',
    account_non_expired     tinyint(1)                                    not null default 1 comment '账户是否未过期',
    account_non_locked      tinyint(1)                                    not null default 1 comment '账户是否未锁定',
    credentials_non_expired tinyint(1)                                    not null default 1 comment '凭证是否未过期',
    enabled                 tinyint(1)                                    not null default 1 comment '是否启用',
    failed_login_attempts   int                                           not null default 0 comment '连续登录失败次数',
    last_login_time         timestamp                                     null     default null comment '最后登录时间',
    last_login_ip           varchar(64)                                            default null comment '最后登录ip',
    last_login_app          bigint                                                 default null comment '最后登录系统',
    password_change_time    timestamp                                     not null default current_timestamp comment '密码修改时间',
    create_by               bigint                                                 default null comment '创建人',
    create_time             timestamp                                     not null default current_timestamp comment '创建时间',
    update_by               bigint                                                 default null comment '更新人',
    update_time             timestamp                                     not null default current_timestamp on update current_timestamp comment '更新时间',
    delete_flag             tinyint(1)                                    not null default 0 comment '删除标志',
    version                 int                                           not null default 1 comment '乐观锁版本号',
    remark                  varchar(500)                                           default null comment '备注',
    primary key (id),
    unique key uk_tenant_username (tenant_id, username),
    unique key uk_email (email),
    unique key uk_phone (phone),
    key idx_tenant_status (tenant_id, status),
    key idx_dept_id (dept_id),
    key idx_default_app_id (default_app_id),
    key idx_last_login_time (last_login_time),
    key idx_delete_flag (delete_flag),
    constraint fk_user_tenant foreign key (tenant_id) references sys_tenant (id) on delete cascade
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='用户表';

# 8. 用户系统关联表 - 优化版
drop table if exists sys_user_app;
create table sys_user_app
(
    id          bigint                               not null auto_increment comment 'id',
    tenant_id   bigint                               not null default 0 comment '租户id',
    user_id     bigint                               not null comment '用户id',
    app_id      bigint                               not null comment '系统id',
    status      enum ('active','inactive','expired') not null default 'active' comment '状态',
    expire_time timestamp                            null     default null comment '到期时间',
    create_by   bigint                                        default null comment '创建人',
    create_time timestamp                            not null default current_timestamp comment '创建时间',
    update_by   bigint                                        default null comment '更新人',
    update_time timestamp                            not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id),
    unique key uk_tenant_user_app (tenant_id, user_id, app_id),
    key idx_user_id (user_id),
    key idx_app_id (app_id),
    key idx_expire_time (expire_time),
    constraint fk_user_app_user foreign key (user_id) references sys_user (id) on delete cascade,
    constraint fk_user_app_application foreign key (app_id) references sys_application (id) on delete cascade
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='用户系统关联表';

# 9. 角色表 - 优化版
drop table if exists sys_role;
create table sys_role
(
    id          bigint                                               not null auto_increment comment '角色id',
    tenant_id   bigint                                               not null default 0 comment '租户id(0表示平台角色)',
    app_id      bigint                                               not null default 0 comment '系统id(0表示通用角色)',
    role_code   varchar(64)                                          not null comment '角色编码',
    role_name   varchar(64)                                          not null comment '角色名称',
    role_level  int                                                  not null default 1 comment '角色级别(数字越小级别越高)',
    data_scope  enum ('all','custom','dept','dept_and_child','self') not null default 'all' comment '数据范围',
    is_system   tinyint(1)                                           not null default 0 comment '是否系统角色',
    is_default  tinyint(1)                                           not null default 0 comment '是否默认角色',
    status      enum ('active','inactive')                           not null default 'active' comment '状态',
    create_by   bigint                                                        default null comment '创建人',
    create_time timestamp                                            not null default current_timestamp comment '创建时间',
    update_by   bigint                                                        default null comment '更新人',
    update_time timestamp                                            not null default current_timestamp on update current_timestamp comment '更新时间',
    delete_flag tinyint(1)                                           not null default 0 comment '删除标志',
    version     int                                                  not null default 1 comment '乐观锁版本号',
    remark      varchar(500)                                                  default null comment '备注',
    primary key (id),
    unique key uk_tenant_app_role_code (tenant_id, app_id, role_code),
    key idx_tenant_app_status (tenant_id, app_id, status),
    key idx_role_level (role_level),
    key idx_delete_flag (delete_flag)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='角色表';

# 10. 权限表 - 优化版
drop table if exists sys_permission;
create table sys_permission
(
    id              bigint                              not null auto_increment comment '权限id',
    tenant_id       bigint                              not null default 0 comment '租户id(0表示平台权限)',
    app_id          bigint                              not null default 0 comment '系统id(0表示通用权限)',
    parent_id       bigint                              not null default 0 comment '父权限id',
    permission_code varchar(128)                        not null comment '权限编码',
    permission_name varchar(64)                         not null comment '权限名称',
    permission_type enum ('menu','button','api','data') not null default 'menu' comment '权限类型',
    path            varchar(255)                                 default null comment '路由路径',
    component       varchar(255)                                 default null comment '组件路径',
    icon            varchar(64)                                  default null comment '图标',
    sort_order      int                                 not null default 0 comment '排序',
    level           int                                 not null default 1 comment '层级',
    is_system       tinyint(1)                          not null default 0 comment '是否系统权限',
    is_external     tinyint(1)                          not null default 0 comment '是否外部链接',
    status          enum ('active','inactive')          not null default 'active' comment '状态',
    visible         tinyint(1)                          not null default 1 comment '是否可见',
    create_by       bigint                                       default null comment '创建人',
    create_time     timestamp                           not null default current_timestamp comment '创建时间',
    update_by       bigint                                       default null comment '更新人',
    update_time     timestamp                           not null default current_timestamp on update current_timestamp comment '更新时间',
    delete_flag     tinyint(1)                          not null default 0 comment '删除标志',
    version         int                                 not null default 1 comment '乐观锁版本号',
    remark          varchar(500)                                 default null comment '备注',
    primary key (id),
    unique key uk_tenant_app_permission_code (tenant_id, app_id, permission_code),
    key idx_tenant_app_status (tenant_id, app_id, status),
    key idx_parent_id (parent_id),
    key idx_permission_type (permission_type),
    key idx_sort_order (sort_order),
    key idx_delete_flag (delete_flag)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='权限表';

# 11. 用户角色关联表 - 优化版
drop table if exists sys_user_role;
create table sys_user_role
(
    id          bigint    not null auto_increment comment 'id',
    tenant_id   bigint    not null default 0 comment '租户id',
    app_id      bigint    not null default 0 comment '系统id',
    user_id     bigint    not null comment '用户id',
    role_id     bigint    not null comment '角色id',
    expire_time timestamp null     default null comment '到期时间',
    create_by   bigint             default null comment '创建人',
    create_time timestamp not null default current_timestamp comment '创建时间',
    primary key (id),
    unique key uk_tenant_app_user_role (tenant_id, app_id, user_id, role_id),
    key idx_user_id (user_id),
    key idx_role_id (role_id),
    key idx_expire_time (expire_time),
    constraint fk_user_role_user foreign key (user_id) references sys_user (id) on delete cascade,
    constraint fk_user_role_role foreign key (role_id) references sys_role (id) on delete cascade
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='用户角色关联表';

# 12. 角色权限关联表 - 优化版
drop table if exists sys_role_permission;
create table sys_role_permission
(
    id            bigint    not null auto_increment comment 'id',
    tenant_id     bigint    not null default 0 comment '租户id',
    app_id        bigint    not null default 0 comment '系统id',
    role_id       bigint    not null comment '角色id',
    permission_id bigint    not null comment '权限id',
    create_by     bigint             default null comment '创建人',
    create_time   timestamp not null default current_timestamp comment '创建时间',
    primary key (id),
    unique key uk_tenant_app_role_permission (tenant_id, app_id, role_id, permission_id),
    key idx_role_id (role_id),
    key idx_permission_id (permission_id),
    constraint fk_role_permission_role foreign key (role_id) references sys_role (id) on delete cascade,
    constraint fk_role_permission_permission foreign key (permission_id) references sys_permission (id) on delete cascade
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='角色权限关联表';

# 13. 部门表 - 优化版
drop table if exists sys_dept;
create table sys_dept
(
    id          bigint                     not null auto_increment comment '部门id',
    tenant_id   bigint                     not null default 0 comment '租户id',
    parent_id   bigint                     not null default 0 comment '父部门id',
    dept_code   varchar(64)                         default null comment '部门编码',
    dept_name   varchar(64)                not null comment '部门名称',
    dept_path   varchar(512)                        default null comment '部门路径(用于快速查询子部门)',
    dept_level  int                        not null default 1 comment '部门层级',
    leader      varchar(64)                         default null comment '负责人',
    phone       varchar(32)                         default null comment '联系电话',
    email       varchar(128)                        default null comment '邮箱',
    sort_order  int                        not null default 0 comment '排序',
    status      enum ('active','inactive') not null default 'active' comment '状态',
    create_by   bigint                              default null comment '创建人',
    create_time timestamp                  not null default current_timestamp comment '创建时间',
    update_by   bigint                              default null comment '更新人',
    update_time timestamp                  not null default current_timestamp on update current_timestamp comment '更新时间',
    delete_flag tinyint(1)                 not null default 0 comment '删除标志',
    version     int                        not null default 1 comment '乐观锁版本号',
    remark      varchar(500)                        default null comment '备注',
    primary key (id),
    key idx_tenant_parent_status (tenant_id, parent_id, status),
    key idx_dept_path (dept_path),
    key idx_sort_order (sort_order),
    key idx_delete_flag (delete_flag),
    constraint fk_dept_tenant foreign key (tenant_id) references sys_tenant (id) on delete cascade
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='部门表';

# 14. 岗位表 - 优化版
drop table if exists sys_post;
create table sys_post
(
    id          bigint                     not null auto_increment comment '岗位id',
    tenant_id   bigint                     not null default 0 comment '租户id',
    post_code   varchar(64)                not null comment '岗位编码',
    post_name   varchar(64)                not null comment '岗位名称',
    post_level  int                        not null default 1 comment '岗位级别',
    sort_order  int                        not null default 0 comment '排序',
    status      enum ('active','inactive') not null default 'active' comment '状态',
    create_by   bigint                              default null comment '创建人',
    create_time timestamp                  not null default current_timestamp comment '创建时间',
    update_by   bigint                              default null comment '更新人',
    update_time timestamp                  not null default current_timestamp on update current_timestamp comment '更新时间',
    delete_flag tinyint(1)                 not null default 0 comment '删除标志',
    version     int                        not null default 1 comment '乐观锁版本号',
    remark      varchar(500)                        default null comment '备注',
    primary key (id),
    unique key uk_tenant_post_code (tenant_id, post_code),
    key idx_tenant_status (tenant_id, status),
    key idx_post_level (post_level),
    key idx_delete_flag (delete_flag),
    constraint fk_post_tenant foreign key (tenant_id) references sys_tenant (id) on delete cascade
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='岗位表';

# 15. 用户岗位关联表 - 优化版
drop table if exists sys_user_post;
create table sys_user_post
(
    id          bigint     not null auto_increment comment 'id',
    tenant_id   bigint     not null default 0 comment '租户id',
    user_id     bigint     not null comment '用户id',
    post_id     bigint     not null comment '岗位id',
    is_primary  tinyint(1) not null default 0 comment '是否主岗位',
    create_by   bigint              default null comment '创建人',
    create_time timestamp  not null default current_timestamp comment '创建时间',
    primary key (id),
    unique key uk_tenant_user_post (tenant_id, user_id, post_id),
    key idx_user_id (user_id),
    key idx_post_id (post_id),
    constraint fk_user_post_user foreign key (user_id) references sys_user (id) on delete cascade,
    constraint fk_user_post_post foreign key (post_id) references sys_post (id) on delete cascade
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='用户岗位关联表';

# 16. 数据字典类型表 - 优化版
drop table if exists sys_dict_type;
create table sys_dict_type
(
    id          bigint                     not null auto_increment comment '字典类型id',
    tenant_id   bigint                     not null default 0 comment '租户id(0表示平台字典)',
    dict_type   varchar(128)               not null comment '字典类型',
    dict_name   varchar(128)               not null comment '字典名称',
    is_system   tinyint(1)                 not null default 0 comment '是否系统字典',
    status      enum ('active','inactive') not null default 'active' comment '状态',
    create_by   bigint                              default null comment '创建人',
    create_time timestamp                  not null default current_timestamp comment '创建时间',
    update_by   bigint                              default null comment '更新人',
    update_time timestamp                  not null default current_timestamp on update current_timestamp comment '更新时间',
    delete_flag tinyint(1)                 not null default 0 comment '删除标志',
    version     int                        not null default 1 comment '乐观锁版本号',
    remark      varchar(500)                        default null comment '备注',
    primary key (id),
    unique key uk_tenant_dict_type (tenant_id, dict_type),
    key idx_tenant_status (tenant_id, status),
    key idx_delete_flag (delete_flag)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='数据字典类型表';

# 17. 数据字典数据表 - 优化版
drop table if exists sys_dict_data;
create table sys_dict_data
(
    id          bigint                     not null auto_increment comment '字典数据id',
    tenant_id   bigint                     not null default 0 comment '租户id(0表示平台字典)',
    dict_type   varchar(128)               not null comment '字典类型',
    dict_label  varchar(128)               not null comment '字典标签',
    dict_value  varchar(128)               not null comment '字典键值',
    dict_sort   int                        not null default 0 comment '字典排序',
    css_class   varchar(128)                        default null comment '样式属性',
    list_class  varchar(128)                        default null comment '表格回显样式',
    is_default  tinyint(1)                 not null default 0 comment '是否默认',
    status      enum ('active','inactive') not null default 'active' comment '状态',
    create_by   bigint                              default null comment '创建人',
    create_time timestamp                  not null default current_timestamp comment '创建时间',
    update_by   bigint                              default null comment '更新人',
    update_time timestamp                  not null default current_timestamp on update current_timestamp comment '更新时间',
    delete_flag tinyint(1)                 not null default 0 comment '删除标志',
    version     int                        not null default 1 comment '乐观锁版本号',
    remark      varchar(500)                        default null comment '备注',
    primary key (id),
    key idx_tenant_dict_type_status (tenant_id, dict_type, status),
    key idx_dict_sort (dict_sort),
    key idx_delete_flag (delete_flag)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='数据字典数据表';

# 18. 系统配置表 - 优化版
drop table if exists sys_config;
create table sys_config
(
    id           bigint                                    not null auto_increment comment '配置id',
    tenant_id    bigint                                    not null default 0 comment '租户id(0表示平台配置)',
    config_name  varchar(128)                              not null comment '配置名称',
    config_key   varchar(128)                              not null comment '配置键名',
    config_value longtext comment '配置键值',
    config_type  enum ('system','user')                    not null default 'system' comment '配置类型',
    value_type   enum ('string','number','boolean','json') not null default 'string' comment '值类型',
    is_system    tinyint(1)                                not null default 0 comment '是否系统配置',
    is_sensitive tinyint(1)                                not null default 0 comment '是否敏感配置',
    create_by    bigint                                             default null comment '创建人',
    create_time  timestamp                                 not null default current_timestamp comment '创建时间',
    update_by    bigint                                             default null comment '更新人',
    update_time  timestamp                                 not null default current_timestamp on update current_timestamp comment '更新时间',
    delete_flag  tinyint(1)                                not null default 0 comment '删除标志',
    version      int                                       not null default 1 comment '乐观锁版本号',
    remark       varchar(500)                                       default null comment '备注',
    primary key (id),
    unique key uk_tenant_config_key (tenant_id, config_key),
    key idx_tenant_type (tenant_id, config_type),
    key idx_delete_flag (delete_flag)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='系统配置表';

# 19. 登录日志表 - 优化版
drop table if exists sys_login_log;
create table sys_login_log
(
    id             bigint                                not null auto_increment comment '日志id',
    tenant_id      bigint                                not null default 0 comment '租户id',
    app_id         bigint                                         default null comment '系统id',
    user_id        bigint                                         default null comment '用户id',
    username       varchar(64)                                    default null comment '用户名',
    login_type     enum ('password','sms','oauth','sso') not null default 'password' comment '登录类型',
    client_info    json                                           default null comment '客户端信息(浏览器、设备等)',
    ip_address     varchar(64)                                    default null comment '登录ip地址',
    location       varchar(255)                                   default null comment '登录地点',
    user_agent     varchar(512)                                   default null comment 'user agent',
    status         enum ('success','failed','blocked')   not null default 'success' comment '登录状态',
    failure_reason varchar(255)                                   default null comment '失败原因',
    session_id     varchar(128)                                   default null comment '会话id',
    login_time     timestamp                             not null default current_timestamp comment '登录时间',
    logout_time    timestamp                             null     default null comment '登出时间',
    primary key (id),
    key idx_tenant_user_time (tenant_id, user_id, login_time),
    key idx_app_id (app_id),
    key idx_username_time (username, login_time),
    key idx_status_time (status, login_time),
    key idx_ip_address (ip_address)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='登录日志表';

# 20. 操作日志表 - 优化版
drop table if exists sys_operation_log;
create table sys_operation_log
(
    id             bigint                                                              not null auto_increment comment '日志id',
    tenant_id      bigint                                                              not null default 0 comment '租户id',
    app_id         bigint                                                                       default null comment '系统id',
    user_id        bigint                                                                       default null comment '用户id',
    trace_id       varchar(64)                                                                  default null comment '链路追踪id',
    module         varchar(64)                                                                  default null comment '操作模块',
    operation      varchar(64)                                                                  default null comment '操作名称',
    business_type  enum ('other','create','update','delete','query','export','import') not null default 'other' comment '业务类型',
    method         varchar(255)                                                                 default null comment '方法名称',
    request_method varchar(10)                                                                  default null comment '请求方式',
    request_url    varchar(512)                                                                 default null comment '请求url',
    request_params json                                                                         default null comment '请求参数',
    response_data  json                                                                         default null comment '返回数据',
    operator_name  varchar(64)                                                                  default null comment '操作人员',
    dept_name      varchar(64)                                                                  default null comment '部门名称',
    ip_address     varchar(64)                                                                  default null comment '操作ip',
    location       varchar(255)                                                                 default null comment '操作地点',
    user_agent     varchar(512)                                                                 default null comment 'user agent',
    status         enum ('success','error')                                            not null default 'success' comment '操作状态',
    error_message  text comment '错误消息',
    cost_time      bigint                                                              not null default 0 comment '执行耗时(毫秒)',
    operation_time timestamp                                                           not null default current_timestamp comment '操作时间',
    primary key (id),
    key idx_tenant_user_time (tenant_id, user_id, operation_time),
    key idx_app_id (app_id),
    key idx_trace_id (trace_id),
    key idx_module_operation (module, operation),
    key idx_business_type_time (business_type, operation_time),
    key idx_status_time (status, operation_time)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='操作日志表';

# 21. 在线用户表 - 优化版
drop table if exists sys_user_online;
create table sys_user_online
(
    id               bigint                              not null auto_increment comment 'id',
    session_id       varchar(128)                        not null comment '会话编号',
    tenant_id        bigint                              not null default 0 comment '租户id',
    app_id           bigint                                       default null comment '系统id',
    user_id          bigint                              not null comment '用户id',
    username         varchar(64)                                  default null comment '登录账号',
    dept_name        varchar(64)                                  default null comment '部门名称',
    client_info      json                                         default null comment '客户端信息',
    ip_address       varchar(64)                                  default null comment '登录ip地址',
    location         varchar(255)                                 default null comment '登录地点',
    user_agent       varchar(512)                                 default null comment 'user agent',
    status           enum ('online','offline','timeout') not null default 'online' comment '在线状态',
    login_time       timestamp                           not null default current_timestamp comment '登录时间',
    last_access_time timestamp                           not null default current_timestamp on update current_timestamp comment '最后访问时间',
    expire_time      timestamp                           not null comment '过期时间',
    primary key (id),
    unique key uk_session_id (session_id),
    key idx_tenant_user (tenant_id, user_id),
    key idx_app_id (app_id),
    key idx_status_time (status, last_access_time),
    key idx_expire_time (expire_time)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='在线用户记录';

# 22. 单点登录票据表 - 优化版
drop table if exists sys_sso_ticket;
create table sys_sso_ticket
(
    id            bigint                           not null auto_increment comment '票据id',
    ticket        varchar(128)                     not null comment '票据值',
    tenant_id     bigint                           not null comment '租户id',
    user_id       bigint                           not null comment '用户id',
    source_app_id bigint                           not null comment '源系统id',
    target_app_id bigint                                    default null comment '目标系统id',
    ticket_type   enum ('login','service','proxy') not null default 'login' comment '票据类型',
    used_count    int                              not null default 0 comment '使用次数',
    max_use_count int                              not null default 1 comment '最大使用次数',
    create_time   timestamp                        not null default current_timestamp comment '创建时间',
    expire_time   timestamp                        not null comment '过期时间',
    used_time     timestamp                        null     default null comment '使用时间',
    ip_address    varchar(64)                               default null comment 'ip地址',
    user_agent    varchar(512)                              default null comment 'user agent',
    primary key (id),
    unique key uk_ticket (ticket),
    key idx_tenant_user (tenant_id, user_id),
    key idx_source_app_id (source_app_id),
    key idx_expire_time (expire_time),
    key idx_ticket_type (ticket_type)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='单点登录票据表';

# 23. 安全策略表 - 新增
drop table if exists sys_security_policy;
create table sys_security_policy
(
    id            bigint                                       not null auto_increment comment '策略id',
    tenant_id     bigint                                       not null default 0 comment '租户id',
    policy_name   varchar(128)                                 not null comment '策略名称',
    policy_type   enum ('password','login','session','access') not null comment '策略类型',
    policy_config json                                         not null comment '策略配置(json格式)',
    is_enabled    tinyint(1)                                   not null default 1 comment '是否启用',
    priority      int                                          not null default 0 comment '优先级',
    create_by     bigint                                                default null comment '创建人',
    create_time   timestamp                                    not null default current_timestamp comment '创建时间',
    update_by     bigint                                                default null comment '更新人',
    update_time   timestamp                                    not null default current_timestamp on update current_timestamp comment '更新时间',
    version       int                                          not null default 1 comment '乐观锁版本号',
    remark        varchar(500)                                          default null comment '备注',
    primary key (id),
    key idx_tenant_type_enabled (tenant_id, policy_type, is_enabled),
    key idx_priority (priority)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='安全策略表';

# 24. 审计日志表 - 新增
drop table if exists sys_audit_log;
create table sys_audit_log
(
    id             bigint                                                not null auto_increment comment '审计id',
    tenant_id      bigint                                                not null default 0 comment '租户id',
    app_id         bigint                                                         default null comment '系统id',
    user_id        bigint                                                         default null comment '用户id',
    trace_id       varchar(64)                                                    default null comment '链路追踪id',
    event_type     enum ('auth','permission','data','config','security') not null comment '事件类型',
    event_name     varchar(128)                                          not null comment '事件名称',
    resource_type  varchar(64)                                                    default null comment '资源类型',
    resource_id    varchar(128)                                                   default null comment '资源id',
    old_value      json                                                           default null comment '变更前值',
    new_value      json                                                           default null comment '变更后值',
    ip_address     varchar(64)                                                    default null comment 'ip地址',
    user_agent     varchar(512)                                                   default null comment 'user agent',
    risk_level     enum ('low','medium','high','critical')               not null default 'low' comment '风险等级',
    result         enum ('success','failed','denied')                    not null comment '执行结果',
    failure_reason varchar(255)                                                   default null comment '失败原因',
    event_time     timestamp                                             not null default current_timestamp comment '事件时间',
    primary key (id),
    key idx_tenant_event_time (tenant_id, event_type, event_time),
    key idx_user_event_time (user_id, event_time),
    key idx_trace_id (trace_id),
    key idx_risk_level_time (risk_level, event_time),
    key idx_resource (resource_type, resource_id)
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
  row_format = dynamic comment ='审计日志表';

# 25. 创建视图 - 优化查询性能
create view v_user_permission as
select u.id as user_id,
       u.tenant_id,
       u.username,
       r.id as role_id,
       r.role_code,
       r.app_id,
       p.id as permission_id,
       p.permission_code,
       p.permission_type
from sys_user u
         join sys_user_role ur on u.id = ur.user_id
         join sys_role r on ur.role_id = r.id
         join sys_role_permission rp on r.id = rp.role_id
         join sys_permission p on rp.permission_id = p.id
where u.status = 'active'
  and u.delete_flag = 0
  and r.status = 'active'
  and r.delete_flag = 0
  and p.status = 'active'
  and p.delete_flag = 0;

# 26. 创建存储过程 - 用户权限检查
delimiter //
create procedure sp_check_user_permission(
    in p_user_id bigint,
    in p_app_id bigint,
    in p_permission_code varchar(128),
    out p_has_permission tinyint
)
begin
    declare v_count int default 0;

    select count(1)
    into v_count
    from v_user_permission
    where user_id = p_user_id
      and (app_id = p_app_id or app_id = 0)
      and permission_code = p_permission_code;

    set p_has_permission = if(v_count > 0, 1, 0);
end //
delimiter ;

# 27. 创建触发器 - 自动更新用户数量
delimiter //
create trigger tr_user_count_insert
    after insert
    on sys_user
    for each row
begin
    if new.tenant_id > 0 then
        update sys_tenant
        set current_user_count = current_user_count + 1,
            version            = version + 1
        where id = new.tenant_id;
    end if;
end //

create trigger tr_user_count_delete
    after update
    on sys_user
    for each row
begin
    if old.delete_flag = 0 and new.delete_flag = 1 and new.tenant_id > 0 then
        update sys_tenant
        set current_user_count = current_user_count - 1,
            version            = version + 1
        where id = new.tenant_id;
    end if;
end //
delimiter ;

# 28. 创建索引优化
create index idx_user_login_performance on sys_user (tenant_id, username, status, delete_flag);
create index idx_permission_query_performance on sys_permission (tenant_id, app_id, permission_type, status, delete_flag);
create index idx_role_query_performance on sys_role (tenant_id, app_id, status, delete_flag);
create index idx_log_cleanup on sys_login_log (login_time);
create index idx_operation_log_cleanup on sys_operation_log (operation_time);

# 29. 插入优化后的初始数据
# 插入默认租户套餐
insert into sys_tenant_package (id, package_code, package_name, package_type, max_user_count, storage_quota,
                                feature_list, price_monthly, price_yearly, trial_days, sort_order, status, create_by,
                                remark)
values (1, 'free', '免费版', 'free', 10, 1073741824, '[
  "user_management",
  "basic_auth"
]', 0.00, 0.00, 0, 1, 1, 1, '免费版套餐'),
       (2, 'basic', '基础版', 'paid', 50, 5368709120, '[
         "user_management",
         "basic_auth",
         "dept_management",
         "role_management"
       ]', 99.00, 990.00, 30, 2, 1, 1, '基础版套餐'),
       (3, 'standard', '标准版', 'paid', 200, 21474836480, '[
         "user_management",
         "basic_auth",
         "dept_management",
         "role_management",
         "log_audit",
         "api_access"
       ]', 299.00, 3588.00, 14, 3, 1, 1, '标准版套餐'),
       (4, 'enterprise', '企业版', 'paid', -1, -1, '[
         "user_management",
         "basic_auth",
         "dept_management",
         "role_management",
         "log_audit",
         "api_access",
         "advanced_security",
         "custom_integration"
       ]', 999.00, 11988.00, 7, 4, 1, 1, '企业版套餐');

# 插入平台管理租户（租户id为0的特殊租户）
insert into sys_tenant (id, tenant_code, tenant_name, tenant_type, tenant_level, contact_name, contact_email,
                        max_user_count, status, create_by, remark)
values (0, 'platform', '平台管理', 1, 4, 'platform admin', 'platform@athena.com', -1, 1, 1, '平台管理租户');

# 插入示例企业租户
insert into sys_tenant (id, tenant_code, tenant_name, tenant_type, tenant_level, domain, contact_name, contact_phone,
                        contact_email, max_user_count, status, create_by, remark)
values (1, 'demo_corp', '演示企业', 1, 2, 'demo.athena.com', '张三', '13800138000', 'demo@athena.com', 100, 1, 1,
        '演示企业租户');

# 插入平台超级管理员用户
insert into sys_user (id, tenant_id, username, password, nick_name, real_name, email, phone, dept_id, is_tenant_admin,
                      status, create_by, remark)
values (1, 0, 'platform_admin', '$2a$10$7jb720yubvsofvvwbmytwojnjqkt4kqjqb.nc8q9b1.fy/l2v3c6e', '平台管理员',
        'platform administrator', 'platform@athena.com', '13888888888', 1, 1, 1, 1, '平台超级管理员');

# 插入租户管理员用户
insert into sys_user (id, tenant_id, username, password, nick_name, real_name, email, phone, dept_id, is_tenant_admin,
                      status, create_by, remark)
values (2, 1, 'admin', '$2a$10$7jb720yubvsofvvwbmytwojnjqkt4kqjqb.nc8q9b1.fy/l2v3c6e', '租户管理员',
        'tenant administrator', 'admin@demo.athena.com', '13800138000', 2, 1, 1, 1, '租户管理员');

# 插入根部门
insert into sys_dept (id, tenant_id, parent_id, dept_name, leader, phone, email, sort_order, status, create_by, remark)
values (1, 0, 0, '平台管理', 'platform_admin', '13888888888', 'platform@athena.com', 0, 1, 1, '平台管理部门'),
       (2, 1, 0, '演示企业', 'admin', '13800138000', 'admin@demo.athena.com', 0, 1, 1, '演示企业根部门');

# 插入平台角色
insert into sys_role (id, tenant_id, app_id, role_code, role_name, role_level, data_scope, is_system, is_default,
                      status, create_by, remark)
values (1, 0, 0, 'role_platform_admin', '平台超级管理员', 1, 1, 1, 0, 1, 1, '平台超级管理员角色'),
       (2, 0, 0, 'role_tenant_admin', '租户管理员', 2, 1, 1, 1, 1, 1, '租户管理员角色'),
       (3, 0, 2, 'role_iam_admin', 'iam管理员', 1, 1, 1, 0, 1, 1, 'iam系统管理员'),
       (4, 0, 2, 'role_iam_user', 'iam用户', 2, 5, 1, 1, 1, 1, 'iam普通用户'),
       (5, 1, 0, 'role_tenant_admin', '租户管理员', 1, 1, 0, 1, 1, 1, '租户管理员角色'),
       (6, 1, 0, 'role_tenant_user', '租户普通用户', 2, 5, 0, 1, 1, 1, '租户普通用户角色'),
       (7, 1, 3, 'role_oa_admin', 'oa管理员', 1, 1, 0, 0, 1, 1, 'oa系统管理员'),
       (8, 1, 3, 'role_oa_user', 'oa用户', 2, 5, 0, 1, 1, 1, 'oa普通用户'),
       (9, 1, 4, 'role_crm_admin', 'crm管理员', 1, 1, 0, 0, 1, 1, 'crm系统管理员'),
       (10, 1, 4, 'role_crm_sales', 'crm销售', 2, 3, 0, 1, 1, 1, 'crm销售人员'),
       (11, 1, 4, 'role_crm_user', 'crm用户', 3, 5, 0, 0, 1, 1, 'crm普通用户'),
       (12, 1, 5, 'role_hrm_admin', 'hrm管理员', 1, 1, 0, 0, 1, 1, 'hrm系统管理员'),
       (13, 1, 5, 'role_hrm_hr', 'hrm人事', 2, 3, 0, 1, 1, 1, 'hrm人事专员'),
       (14, 1, 5, 'role_hrm_user', 'hrm员工', 3, 5, 0, 1, 1, 1, 'hrm普通员工');

# 插入权限信息（多系统支持）
insert into sys_permission (id, tenant_id, app_id, parent_id, permission_code, permission_name, permission_type, path,
                            component, icon, sort_order, is_system, status, visible, create_by, remark)
values
-- 平台管理权限
(1, 0, 1, 0, 'platform', '平台管理', 1, '/platform', '', 'platform', 1, 1, 1, 1, 1, '平台管理目录'),
(2, 0, 1, 1, 'platform:tenant', '租户管理', 1, '/platform/tenant', 'platform/tenant/index', 'tenant', 1, 1, 1, 1, 1,
 '租户管理菜单'),
(3, 0, 1, 1, 'platform:package', '套餐管理', 1, '/platform/package', 'platform/package/index', 'package', 2, 1, 1, 1, 1,
 '套餐管理菜单'),
(4, 0, 1, 1, 'platform:app', '应用管理', 1, '/platform/app', 'platform/app/index', 'app', 3, 1, 1, 1, 1,
 '应用管理菜单'),
(5, 0, 1, 1, 'platform:monitor', '平台监控', 1, '/platform/monitor', 'platform/monitor/index', 'monitor', 4, 1, 1, 1, 1,
 '平台监控菜单'),

-- iam系统权限
(10, 0, 2, 0, 'iam', 'iam管理', 1, '/iam', '', 'user', 1, 1, 1, 1, 1, 'iam管理目录'),
(11, 0, 2, 10, 'iam:user', '用户管理', 1, '/iam/user', 'iam/user/index', 'user', 1, 1, 1, 1, 1, '用户管理菜单'),
(12, 0, 2, 10, 'iam:role', '角色管理', 1, '/iam/role', 'iam/role/index', 'peoples', 2, 1, 1, 1, 1, '角色管理菜单'),
(13, 0, 2, 10, 'iam:permission', '权限管理', 1, '/iam/permission', 'iam/permission/index', 'tree-table', 3, 1, 1, 1, 1,
 '权限管理菜单'),
(14, 0, 2, 10, 'iam:dept', '部门管理', 1, '/iam/dept', 'iam/dept/index', 'tree', 4, 1, 1, 1, 1, '部门管理菜单'),

-- oa系统权限
(20, 1, 3, 0, 'oa', 'oa系统', 1, '/oa', '', 'office', 1, 0, 1, 1, 1, 'oa系统目录'),
(21, 1, 3, 20, 'oa:workflow', '工作流', 1, '/oa/workflow', 'oa/workflow/index', 'flow', 1, 0, 1, 1, 1, '工作流管理'),
(22, 1, 3, 20, 'oa:document', '文档管理', 1, '/oa/document', 'oa/document/index', 'file', 2, 0, 1, 1, 1, '文档管理'),
(23, 1, 3, 20, 'oa:meeting', '会议管理', 1, '/oa/meeting', 'oa/meeting/index', 'meeting', 3, 0, 1, 1, 1, '会议管理'),

-- crm系统权限
(30, 1, 4, 0, 'crm', 'crm系统', 1, '/crm', '', 'customer', 1, 0, 1, 1, 1, 'crm系统目录'),
(31, 1, 4, 30, 'crm:customer', '客户管理', 1, '/crm/customer', 'crm/customer/index', 'customer', 1, 0, 1, 1, 1,
 '客户管理'),
(32, 1, 4, 30, 'crm:leads', '线索管理', 1, '/crm/leads', 'crm/leads/index', 'leads', 2, 0, 1, 1, 1, '线索管理'),
(33, 1, 4, 30, 'crm:opportunity', '商机管理', 1, '/crm/opportunity', 'crm/opportunity/index', 'opportunity', 3, 0, 1, 1,
 1, '商机管理'),

-- hrm系统权限
(40, 1, 5, 0, 'hrm', 'hrm系统', 1, '/hrm', '', 'team', 1, 0, 1, 1, 1, 'hrm系统目录'),
(41, 1, 5, 40, 'hrm:employee', '员工管理', 1, '/hrm/employee', 'hrm/employee/index', 'employee', 1, 0, 1, 1, 1,
 '员工管理'),
(42, 1, 5, 40, 'hrm:attendance', '考勤管理', 1, '/hrm/attendance', 'hrm/attendance/index', 'attendance', 2, 0, 1, 1, 1,
 '考勤管理'),
(43, 1, 5, 40, 'hrm:salary', '薪资管理', 1, '/hrm/salary', 'hrm/salary/index', 'salary', 3, 0, 1, 1, 1, '薪资管理');

# 插入用户角色关联
insert into sys_user_role (tenant_id, app_id, user_id, role_id, create_by)
values
-- 平台管理员
(0, 0, 1, 1, 1),
(0, 1, 1, 1, 1),
(0, 2, 1, 3, 1),

-- 租户管理员
(1, 0, 2, 5, 1),
(1, 2, 2, 4, 1),
(1, 3, 2, 7, 1),
(1, 4, 2, 9, 1),
(1, 5, 2, 12, 1);

# 插入用户系统关联
insert into sys_user_app (tenant_id, user_id, app_id, status, create_by)
values
-- 平台管理员可访问所有平台系统
(0, 1, 1, 1, 1),
(0, 1, 2, 1, 1),

-- 租户管理员可访问租户下的所有系统
(1, 2, 2, 1, 1),
(1, 2, 3, 1, 1),
(1, 2, 4, 1, 1),
(1, 2, 5, 1, 1);

# 插入角色权限关联
insert into sys_role_permission (tenant_id, app_id, role_id, permission_id, create_by)
select 0, 0, 1, id, 1
from sys_permission
where tenant_id = 0;
-- 平台超级管理员拥有所有权限

# 插入系统配置示例
insert into sys_app_config (tenant_id, app_id, config_key, config_value, config_type, create_by, remark)
values
-- oa系统配置
(1, 3, 'workflow.maxsteps', '10', 'number', 1, '工作流最大步骤数'),
(1, 3, 'document.maxsize', '50mb', 'string', 1, '文档最大大小'),
(1, 3, 'meeting.autoreminder', 'true', 'boolean', 1, '会议自动提醒'),

-- crm系统配置
(1, 4, 'customer.importlimit', '1000', 'number', 1, '客户导入限制'),
(1, 4, 'leads.autoassign', 'true', 'boolean', 1, '线索自动分配'),
(1, 4, 'opportunity.stages', '["初期接触","需求确认","方案制定","商务谈判","合同签订"]', 'json', 1, '商机阶段配置'),

-- hrm系统配置
(1, 5, 'attendance.workdays', '5', 'number', 1, '工作日天数'),
(1, 5, 'salary.currency', 'cny', 'string', 1, '薪资货币单位'),
(1, 5, 'employee.probationperiod', '90', 'number', 1, '试用期天数');

# 插入字典数据
insert into sys_dict_type (tenant_id, dict_type, dict_name, is_system, status, create_by, remark)
values (0, 'sys_user_sex', '用户性别', 1, 1, 1, '用户性别列表'),
       (0, 'sys_show_hide', '显示状态', 1, 1, 1, '菜单状态列表'),
       (0, 'sys_normal_disable', '系统状态', 1, 1, 1, '登录状态列表'),
       (0, 'sys_tenant_status', '租户状态', 1, 1, 1, '租户状态列表'),
       (0, 'sys_tenant_type', '租户类型', 1, 1, 1, '租户类型列表'),
       (0, 'sys_yes_no', '系统是否', 1, 1, 1, '系统是否列表');

# 插入基础字典数据（平台级别）
insert into sys_dict_data (tenant_id, dict_type, dict_label, dict_value, dict_sort, is_default, status, create_by,
                           remark)
values (0, 'sys_user_sex', '男', '1', 1, 1, 1, 1, '性别男'),
       (0, 'sys_user_sex', '女', '2', 2, 0, 1, 1, '性别女'),
       (0, 'sys_user_sex', '未知', '0', 3, 0, 1, 1, '性别未知'),
       (0, 'sys_show_hide', '显示', '1', 1, 1, 1, 1, '显示菜单'),
       (0, 'sys_show_hide', '隐藏', '0', 2, 0, 1, 1, '隐藏菜单'),
       (0, 'sys_normal_disable', '正常', '1', 1, 1, 1, 1, '正常状态'),
       (0, 'sys_normal_disable', '停用', '0', 2, 0, 1, 1, '停用状态'),
       (0, 'sys_tenant_status', '启用', '1', 1, 1, 1, 1, '租户启用'),
       (0, 'sys_tenant_status', '禁用', '0', 2, 0, 1, 1, '租户禁用'),
       (0, 'sys_tenant_status', '过期', '2', 3, 0, 1, 1, '租户过期'),
       (0, 'sys_tenant_status', '欠费', '3', 4, 0, 1, 1, '租户欠费'),
       (0, 'sys_tenant_type', '企业', '1', 1, 1, 1, 1, '企业租户'),
       (0, 'sys_tenant_type', '个人', '2', 2, 0, 1, 1, '个人租户'),
       (0, 'sys_tenant_type', '试用', '3', 3, 0, 1, 1, '试用租户'),
       (0, 'sys_yes_no', '是', '1', 1, 1, 1, 1, '系统默认是'),
       (0, 'sys_yes_no', '否', '0', 2, 0, 1, 1, '系统默认否');

# 插入基础配置（平台级别）
insert into sys_config (tenant_id, config_name, config_key, config_value, config_type, is_system, create_by, remark)
values (0, '平台名称', 'sys.platform.name', '雅典娜iam平台', 0, 1, 1, '平台名称配置'),
       (0, '用户管理-账号初始密码', 'sys.user.initpassword', '123456', 0, 1, 1, '初始化密码 123456'),
       (0, '租户管理-默认套餐', 'sys.tenant.defaultpackage', 'free', 0, 1, 1, '新租户默认套餐'),
       (0, '账号自助-验证码开关', 'sys.account.captchaenabled', 'true', 0, 1, 1,
        '是否开启验证码功能（true开启，false关闭）'),
       (0, '账号自助-是否开启用户注册功能', 'sys.account.registeruser', 'false', 0, 1, 1,
        '是否开启注册用户功能（true开启，false关闭）');

# 插入租户配置示例
insert into sys_tenant_config (tenant_id, config_key, config_value, config_type, create_by, remark)
values (1, 'theme.primarycolor', '#1890ff', 'string', 1, '主题色配置'),
       (1, 'login.maxattempts', '5', 'number', 1, '最大登录尝试次数'),
       (1, 'session.timeout', '30', 'number', 1, '会话超时时间（分钟）'),
       (1, 'notification.email.enabled', 'true', 'boolean', 1, '邮件通知开关');
