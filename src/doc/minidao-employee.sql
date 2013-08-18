/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50037
Source Host           : localhost:3306
Source Database       : jeecg-test

Target Server Type    : MYSQL
Target Server Version : 50037
File Encoding         : 65001

Date: 2013-08-17 16:05:06
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `employee`
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` varchar(36) NOT NULL default '' COMMENT '主键',
  `empno` varchar(120) default NULL COMMENT '雇员编号',
  `NAME` varchar(32) NOT NULL COMMENT '雇员名',
  `AGE` int(11) default NULL COMMENT '年龄',
  `BIRTHDAY` datetime default NULL COMMENT '生日',
  `SALARY` decimal(19,2) default NULL COMMENT '工资',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES ('1', '001', '张开忠', '20', '2013-08-17 11:10:05', '8900.55');
INSERT INTO `employee` VALUES ('2', '', '张开忠2', '0', null, '0.00');
INSERT INTO `employee` VALUES ('3', '', '张开忠2', null, null, null);
INSERT INTO `employee` VALUES ('4', '', '张开忠2', null, null, null);
INSERT INTO `employee` VALUES ('5', '', '张开忠2', null, null, null);
INSERT INTO `employee` VALUES ('6', '', '张开忠2', null, null, null);
INSERT INTO `employee` VALUES ('7', '', '张开忠2', null, null, null);
INSERT INTO `employee` VALUES ('8', '', '张开忠2', null, null, null);
