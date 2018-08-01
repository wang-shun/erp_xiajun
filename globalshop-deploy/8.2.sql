ALTER TABLE `item_sku` ADD COLUMN `sku_rate` DOUBLE(10,2) NOT NULL DEFAULT '0' COMMENT '代理佣金比例';

ALTER TABLE `item` ADD COLUMN `commission_rate` varchar(64) DEFAULT NULL COMMENT '佣金比率';



CREATE TABLE IF NOT EXISTS `mall_commision_apply` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `company_no` varchar(64) DEFAULT NULL,
  `order_no` varchar(64) DEFAULT NULL,
  `sub_order_no` varchar(64) DEFAULT NULL,
  `commision` varchar(4096) DEFAULT NULL,
  `status` varchar(2) DEFAULT NULL COMMENT '0-新建; 1-订单签收确认; 2-清分',
  `is_sync` int(2) DEFAULT '0' COMMENT '是否同步',
  `order_time` varchar(64) DEFAULT NULL,
  `order_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `receive_time` varchar(64) DEFAULT NULL COMMENT '签收时间',
  `receive_date` datetime DEFAULT NULL COMMENT '签收日期',
  `share_user_id` varchar(64) DEFAULT NULL,
  `is_del` tinyint(1) DEFAULT '0',
  `creator` varchar(32) DEFAULT NULL,
  `modifier` varchar(32) DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modify` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `mall_sub_order_snapshot` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `company_no` varchar(64) DEFAULT NULL,
  `order_no` varchar(64) DEFAULT NULL,
  `sub_order_no` varchar(64) DEFAULT NULL,
  `item_code` varchar(64) DEFAULT NULL,
  `item_name` varchar(128) DEFAULT NULL,
  `sku_code` varchar(64) DEFAULT NULL,
  `sku_pic` varchar(512) DEFAULT NULL,
  `upc` varchar(64) DEFAULT NULL,
  `scale` varchar(128) DEFAULT NULL COMMENT '规格，用分号隔开',
  `ext` varchar(4096) DEFAULT NULL,
  `sale_price` decimal(16,2) DEFAULT NULL,
  `is_del` tinyint(1) DEFAULT '0',
  `creator` varchar(32) DEFAULT NULL,
  `modifier` varchar(32) DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modify` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `mall_sale_agent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_no` varchar(64) NOT NULL COMMENT '所属公司id',
  `user_no` varchar(64) NOT NULL COMMENT '唯一userNo，需要跟Auth_User一致',
  `parent_agent` varchar(64) DEFAULT NULL COMMENT '上级代理的userNo，如已是一级代理该值为null',
  `open_id` varchar(64) DEFAULT NULL COMMENT '微信open_id',
  `union_id` varchar(128) DEFAULT NULL COMMENT '微信union_id',
  `agent_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '代理人的微信号/昵称',
  `real_name` varchar(32) DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(32) DEFAULT NULL COMMENT '身份证号号码',
  `head_protrait_url` varchar(256) DEFAULT NULL COMMENT '头像',
  `gender` int(3) DEFAULT NULL COMMENT '0未知,1男,2女',
  `city` varchar(64) DEFAULT NULL,
  `province` varchar(64) DEFAULT NULL,
  `country` varchar(64) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `commission_mode` bigint(18) DEFAULT '0' COMMENT '佣金模式，0为按百分比，1为按金额',
  `commission_value` double(6,2) DEFAULT '0.0' COMMENT '佣金数字值，百分比模式如5%填0.05，金额模式则为金额',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态，0正常，1已解除',
  `join_time` datetime DEFAULT NULL COMMENT '加入时间',
  `last_login_time` datetime DEFAULT NULL,
  `gmt_modify` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifier` varchar(32) NOT NULL,
  `creator` varchar(32) NOT NULL,
  `is_del` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `OPENID` (`open_id`,`union_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='销售代理';


ALTER TABLE applet_config ADD mch_id VARCHAR(64) NULL COMMENT '商户号(微信支付用)';
ALTER TABLE applet_config ADD status VARCHAR(5) NULL COMMENT '支付类型 1.商户版 2.平台版';
ALTER TABLE applet_config ADD pay_key VARCHAR(64) NULL COMMENT '商户版支付秘钥';
ALTER TABLE buyer_task_detail ADD company_no VARCHAR(64) NULL;


CREATE TABLE `commission_sumary_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属公司id',
  `status` int(4) DEFAULT NULL COMMENT '1待结算2可结算3已结算',
  `gmt_modify` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '操作时间',
  `gmt_create` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifier` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `creator` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `settlement_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '结算单号',
  `is_del` tinyint(1) NOT NULL DEFAULT 0,
  `share_user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `settlement` decimal(16, 2) DEFAULT NULL,
  `sub_order_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '绑定订单信息',
  `sale_price` decimal(16, 2) DEFAULT NULL COMMENT '销售额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `commission_sumary_settlement`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属公司id',
  `status` int(4) DEFAULT NULL,
  `gmt_modify` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '操作时间',
  `gmt_create` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifier` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `creator` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `is_del` tinyint(1) NOT NULL DEFAULT 0,
  `settlement_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '结算单号',
  `share_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `share_user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '代理名字',
  `total_price` decimal(16, 2) DEFAULT NULL COMMENT '实付总计',
  `settlement` decimal(16, 2) DEFAULT NULL COMMENT '实际结算',
  `detail_count` int(10) DEFAULT NULL COMMENT '结算订单数',
  `settlement_time` datetime(0) DEFAULT NULL COMMENT '结算时间',
  `remark` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '结算备注',
  `pay_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付方式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `commission_sumary`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属公司id',
  `gmt_modify` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '操作时间',
  `gmt_create` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifier` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `creator` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `is_del` tinyint(1) NOT NULL DEFAULT 0,
  `order_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `sub_order_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `item_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `item_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `sku_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `sku_pic` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `upc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `scale` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `sale_price` decimal(16, 2) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL COMMENT '商品数量',
  `total_settlement` decimal(16, 2) DEFAULT NULL,
  `receiver_info` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '收件信息',
  `order_time` datetime(0) DEFAULT NULL COMMENT '销售时间，下单时间',
  `order_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `order_desc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `status` int(4) DEFAULT NULL COMMENT '0待结算1可结算2已结算',
  `receive_date` datetime(0) DEFAULT NULL COMMENT '签收时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `haidb2new`.`monitor_record`(
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT,
  `app_name` VARCHAR(64) COMMENT '应用名称',
  `monitor_url` VARCHAR(256) COMMENT '监控url',
  `status` VARCHAR(2) COMMENT '0-新建，1-生效，2-暂停',
  `is_del` TINYINT(1) DEFAULT 0,
  `creator` VARCHAR(32),
  `modifier` VARCHAR(32),
  `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `gmt_modify` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=INNODB CHARSET=utf8;

