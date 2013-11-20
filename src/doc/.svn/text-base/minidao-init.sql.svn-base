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


SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(36) NOT NULL default '',
  `name` varchar(120) default NULL,
  `age` int(11) default NULL,
  `content` varchar(10000) default NULL,
  `birthday` date default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('402880e740a6499c0140a6499f720000', '小张', '20', null, '2013-08-22');
INSERT INTO `user` VALUES ('402880e740a6533a0140a6533ce80000', '张代浩', '30', null, null);
INSERT INTO `user` VALUES ('402880e740a653590140a6535c730000', '张代浩1', '11', null, null);
INSERT INTO `user` VALUES ('402880e740a653760140a65379570000', '张代浩2', '22', null, null);
