/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : minidao

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2014-12-09 23:10:56
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` varchar(32) DEFAULT NULL,
  `empno` varchar(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `birthday` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `salary` decimal(10,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES ('1', '1', 'scott', '2014-12-08 22:47:20', '20', null);
INSERT INTO `employee` VALUES ('2', '2', '张代浩的世界1', '2014-12-08 22:47:48', '20', null);
INSERT INTO `employee` VALUES ('3', '001', '张开忠', '2014-12-08 22:48:41', '20', null);
INSERT INTO `employee` VALUES ('4', null, 'scott', '2014-12-08 22:49:57', '20', null);
INSERT INTO `employee` VALUES ('5', null, 'scott', '2014-12-08 22:50:27', '20', null);
INSERT INTO `employee` VALUES ('6', null, 'scott', '2014-12-08 22:51:07', '20', null);
INSERT INTO `employee` VALUES ('7', null, 'scott', '2014-12-08 22:52:16', '20', null);
INSERT INTO `employee` VALUES ('8', null, 'scott', '2014-12-08 22:52:58', '20', null);
INSERT INTO `employee` VALUES ('9', null, 'scott', '2014-12-08 22:57:58', '20', null);
INSERT INTO `employee` VALUES ('10', null, 'scott', '2014-12-08 22:58:24', '20', null);
INSERT INTO `employee` VALUES ('11', null, 'scott', '2014-12-08 23:01:16', '20', null);
INSERT INTO `employee` VALUES ('12', null, 'scott', '2014-12-08 23:01:36', '20', null);
INSERT INTO `employee` VALUES ('13', null, 'scott', '2014-12-08 23:37:13', '20', null);
INSERT INTO `employee` VALUES ('14', null, 'scott', '2014-12-09 20:42:52', '20', null);

-- ----------------------------
-- Table structure for jeecg_demo
-- ----------------------------
DROP TABLE IF EXISTS `jeecg_demo`;
CREATE TABLE `jeecg_demo` (
  `id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `MOBILE_PHONE` varchar(255) DEFAULT NULL,
  `OFFICE_PHONE` varchar(255) DEFAULT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `AGE` int(11) DEFAULT NULL,
  `SALARY` decimal(10,0) DEFAULT NULL,
  `BIRTHDAY` varchar(255) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `SEX` varchar(255) DEFAULT NULL,
  `DEP_ID` int(11) DEFAULT NULL,
  `USER_NAME` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of jeecg_demo
-- ----------------------------
INSERT INTO `jeecg_demo` VALUES ('402880e74a2f2acc014a2f2acf6e0000', null, null, null, null, '30', null, '2014-12-09 21:09:02', null, null, null, '张代浩111');
INSERT INTO `jeecg_demo` VALUES ('402880e74a2f2deb014a2f2dee710000', null, null, null, null, '30', null, '2014-12-09 21:12:26', null, null, null, '小明的数学');
INSERT INTO `jeecg_demo` VALUES ('402880e74a2f2e97014a2f2e9a390000', null, null, null, null, '30', null, '2014-12-09 21:13:10', null, null, null, '小明的数学');
INSERT INTO `jeecg_demo` VALUES ('402880e74a2f2f74014a2f2f77b00000', null, null, null, null, '30', null, '2014-12-09 21:14:07', null, null, null, '小明的数学');
INSERT INTO `jeecg_demo` VALUES ('402880e74a2f2fd8014a2f2fdb1b0000', null, null, null, null, '90', null, '2014-12-09 21:15:44', null, null, null, '李四');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `birthday` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('402880e74a2f3648014a2f364bb10000', '张代浩2', null, null, '22');
INSERT INTO `user` VALUES ('402880e74a2f3678014a2f367b790000', '小张', null, '2014-12-09', '20');
