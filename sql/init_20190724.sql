drop table if exists auto_work_profile;
CREATE TABLE `auto_work_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) NOT NULL COMMENT '名称',
  `content` text NOT NULL COMMENT '内容, 自定义格式',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='系统配置项';


drop table if exists class_info;
CREATE TABLE `class_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_type` tinyint(5) DEFAULT NULL COMMENT '项目',
  `class_message` text  COMMENT 'class信息',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='类信息表';


drop table if exists mail_info;
CREATE TABLE `mail_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_type` int(11) DEFAULT NULL COMMENT '项目',
  `mail_subject` varchar(256)  DEFAULT NULL COMMENT '邮件主题',
  `mail_content` text COMMENT '邮件内容',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件信息表';

drop table if exists mail_info_log;
CREATE TABLE `mail_info_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_type` int(11) DEFAULT NULL COMMENT '项目',
  `mail_subject` varchar(256) DEFAULT NULL COMMENT '邮件主题',
  `mail_content` text COMMENT '邮件内容',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件信息日志表';



