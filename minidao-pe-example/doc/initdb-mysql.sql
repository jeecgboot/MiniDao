DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` varchar(36) NOT NULL default '' COMMENT '主键',
  `empno` varchar(120) default NULL COMMENT '雇员编号',
  `name` varchar(32) NOT NULL COMMENT '雇员名',
  `age` int(11) default NULL COMMENT '年龄',
  `birthday` datetime default NULL COMMENT '生日',
  `salary` decimal(19,2) default NULL COMMENT '工资',
  `create_by` varchar(200) default NULL,
  `create_date` datetime default NULL,
  `update_by` varchar(200) default NULL,
  `update_date` datetime default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES ('33F9ADE7A8B74150821EFF8A2A5EA27B', 'A001', 'scott', '25', '2017-08-18 14:42:01', '5000.00', 'scott', '2017-08-18 14:42:01', null, null);
INSERT INTO `employee` VALUES ('45266BB08B9B45B3B9BA8F9488495623', '200', 'scott', '20', '2017-08-18 14:48:34', '88888.00', 'scott', '2017-08-18 14:48:34', null, null);
INSERT INTO `employee` VALUES ('603D9DB409FE407183156BAA8FA779CD', '200', 'scott', '20', '2017-08-18 14:44:54', '88888.00', 'scott', '2017-08-18 14:44:54', null, null);
INSERT INTO `employee` VALUES ('AD1024E0DAD84D2DB76A82E779F85B76', 'A001', '张代浩的世界', null, '2017-08-18 14:48:34', '5000.00', 'scott', '2017-08-18 14:41:15', 'scott', '2017-08-18 14:48:34');
INSERT INTO `employee` VALUES ('F22B3B5F371E49918B061442010F1C9A', 'A001', 'scott', '25', '2017-08-18 14:48:11', '5000.00', 'scott', '2017-08-18 14:48:11', null, null);
INSERT INTO `employee` VALUES ('F708E15CF2904F6286EF4A72B9E60AED', '200', 'scott', '20', '2017-08-18 14:47:10', '88888.00', 'scott', '2017-08-18 14:47:10', null, null);