CREATE database if NOT EXISTS `minidao-pe` default character set utf8mb4 collate utf8mb4_unicode_ci;
USE `minidao-pe`;

/*
 Navicat Premium Data Transfer

 Source Server         : mysql5.7
 Source Server Type    : MySQL
 Source Server Version : 50738
 Source Host           : 127.0.0.1:3306
 Source Schema         : minidao-pe

 Target Server Type    : MySQL
 Target Server Version : 50738
 File Encoding         : 65001

 Date: 19/12/2023 09:48:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `empno` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '雇员编号',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '雇员名',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `birthday` datetime NULL DEFAULT NULL COMMENT '生日',
  `salary` decimal(19, 2) NULL DEFAULT NULL COMMENT '工资',
  `create_by` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_date` datetime NULL DEFAULT NULL,
  `update_by` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_date` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `id`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (1, '200', 'scott', 20, '2021-07-26 22:01:55', 88888.00, 'scott', '2021-07-26 22:01:55', NULL, NULL);
INSERT INTO `employee` VALUES (2, '200', 'scott', 20, '2021-07-26 22:22:25', 88888.00, 'scott', '2021-07-26 22:22:25', NULL, NULL);
INSERT INTO `employee` VALUES (3, '200', 'scott', 20, '2021-07-26 22:22:33', 88888.00, 'scott', '2021-07-26 22:22:33', NULL, NULL);
INSERT INTO `employee` VALUES (4, '200', 'scott', 20, '2021-07-26 22:22:39', 88888.00, 'scott', '2021-07-26 22:22:39', NULL, NULL);
INSERT INTO `employee` VALUES (5, '200', 'scott', 20, '2021-07-26 22:35:44', 88888.00, 'scott', '2021-07-26 22:35:44', NULL, NULL);
INSERT INTO `employee` VALUES (6, '200', 'scott', 20, '2021-07-26 22:36:17', 88888.00, 'scott', '2021-07-26 22:36:17', NULL, NULL);
INSERT INTO `employee` VALUES (7, '200', 'scott', 20, '2021-07-26 22:36:32', 88888.00, 'scott', '2021-07-26 22:36:32', NULL, NULL);
INSERT INTO `employee` VALUES (8, '200', 'scott', 20, '2021-07-26 22:36:58', 88888.00, 'scott', '2021-07-26 22:36:58', NULL, NULL);
INSERT INTO `employee` VALUES (9, '200', 'scott', 20, '2021-07-26 22:37:45', 88888.00, 'scott', '2021-07-26 22:37:45', NULL, NULL);
INSERT INTO `employee` VALUES (10, '200', 'scott', 20, '2021-07-26 22:39:44', 88888.00, 'scott', '2021-07-26 22:39:44', NULL, NULL);
INSERT INTO `employee` VALUES (11, '200', 'scott', 20, '2021-07-26 22:39:51', 88888.00, 'scott', '2021-07-26 22:39:51', NULL, NULL);
INSERT INTO `employee` VALUES (12, '200', 'scott', 20, '2021-07-26 22:45:12', 88888.00, 'scott', '2021-07-26 22:45:12', NULL, NULL);
INSERT INTO `employee` VALUES (13, '200', 'scott', 20, '2021-12-02 18:03:25', 88888.00, 'scott', '2021-12-02 18:03:25', NULL, NULL);
INSERT INTO `employee` VALUES (14, '2001', 'scott1', 21, '2021-12-02 18:03:54', 88888111.00, 'scott', '2021-12-02 18:03:54', NULL, NULL);
INSERT INTO `employee` VALUES (15, '2001', 'scott1', 21, '2021-12-02 18:07:29', 88888111.00, 'admin', '2021-12-02 18:07:29', NULL, NULL);
INSERT INTO `employee` VALUES (16, '2001', 'scott1', 21, '2023-12-15 11:59:08', 88888111.00, 'admin', '2023-12-15 11:59:08', NULL, NULL);
INSERT INTO `employee` VALUES (17, '200', 'scott', 20, '2023-12-15 12:00:12', 88888.00, 'admin', '2023-12-15 12:00:12', NULL, NULL);

-- ----------------------------
-- Table structure for employee_copy1
-- ----------------------------
DROP TABLE IF EXISTS `employee_copy1`;
CREATE TABLE `employee_copy1`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '主键',
  `empno` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '雇员编号',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '雇员名',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `birthday` datetime NULL DEFAULT NULL COMMENT '生日',
  `salary` decimal(19, 2) NULL DEFAULT NULL COMMENT '工资',
  `create_by` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_date` datetime NULL DEFAULT NULL,
  `update_by` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_date` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee_copy1
-- ----------------------------
INSERT INTO `employee_copy1` VALUES ('100001', NULL, 'spring boot name', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('11000', NULL, 'spring boot name', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('1542722541856', NULL, 'spring boot name', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('1542722566763', NULL, 'spring boot name', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('1542722641812', NULL, 'spring boot name', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('1542722669544', NULL, 'spring boot name', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('1542722671237', NULL, 'spring boot name', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('22A058496ED04C428C2E6A08AC4F0A77', '200', 'scott', 20, '2018-07-04 17:24:06', 88888.00, 'scott', '2018-07-04 17:24:06', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('33F9ADE7A8B74150821EFF8A2A5EA27B', 'A001', 'scott', 25, '2017-08-18 14:42:01', 5000.00, 'scott', '2017-08-18 14:42:01', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('45266BB08B9B45B3B9BA8F9488495623', '200', '张代浩', 20, '2017-08-18 14:48:34', 12000.96, 'scott', '2017-08-18 14:48:34', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('595a4a4418c74aa588ad3a97a7193af5', '200', 'scott', 20, '2021-07-26 21:59:33', 88888.00, 'scott', '2021-07-26 21:59:33', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('603D9DB409FE407183156BAA8FA779CD', '200', 'scott', 20, '2017-08-18 14:44:54', 88888.00, 'scott', '2017-08-18 14:44:54', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('69C2B3A21E224676837266E8913FBCB3', '200', 'scott', 20, '2018-05-28 12:05:25', 88888.00, 'scott', '2018-05-28 12:05:25', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('6B86E541838343938A315B7B4FB9A695', '200', 'scott', 20, '2018-05-28 12:12:03', 88888.00, 'scott', '2018-05-28 12:12:03', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('740A94D751FF4F9C95A9B6117A902FEA', '200', 'scott', 20, '2018-05-28 16:29:34', 88888.00, 'scott', '2018-05-28 16:29:34', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('7768A4DA95214E3B89306F7C5CA9359C', '200', 'scott', 20, '2018-05-28 12:08:56', 88888.00, 'scott', '2018-05-28 12:08:56', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('797A5FA2B27C4A0B9AEF25D1088CFDE8', 'A0100', 'scott100', 25, '2018-05-28 15:56:42', 5000.00, 'scott', '2018-05-28 15:56:42', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('95569EAACBB2405C806985CE72CFF1AA', 'A0100', 'scott100', 25, '2018-05-26 17:45:27', 5000.00, 'scott', '2018-05-26 17:45:27', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('9D365E5AC40B4DAA99B7D5617BE76A03', '200', 'scott', 20, '2021-07-09 17:03:53', 88888.00, 'scott', '2021-07-09 17:03:53', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('AD1024E0DAD84D2DB76A82E779F85B76', 'A001', '张代浩的世界', NULL, '2021-07-09 17:03:53', 5000.00, 'scott', '2017-08-18 14:41:15', 'scott', '2021-07-09 17:03:53');
INSERT INTO `employee_copy1` VALUES ('B118E1D744E0484095E56E65F5400D90', 'A0100', 'scott100', 25, '2018-07-04 17:22:48', 5000.00, 'scott', '2018-07-04 17:22:48', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('CC1309CB05284652BD98EA163B5ED45B', '200', 'scott', 20, '2018-05-28 12:07:36', 88888.00, 'scott', '2018-05-28 12:07:36', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('CFECE7591ACE47F588F93C2B62E10EFC', '200', 'scott', 20, '2018-05-28 12:08:17', 88888.00, 'scott', '2018-05-28 12:08:17', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('D06D76E2585C4BFF8205780B44D4A321', '200', 'scott', 20, '2018-07-04 17:23:16', 88888.00, 'scott', '2018-07-04 17:23:16', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('d7696ead226c4694a33f88016c0f255c', '200', 'scott', 20, '2021-07-26 21:59:58', 88888.00, 'scott', '2021-07-26 21:59:58', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('DB0C9549077D4171A741BE7877540A3F', 'A0100', 'scott100', 25, '2018-05-26 17:43:15', 5000.00, 'scott', '2018-05-26 17:43:15', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('E66AE0FB5ED74DA18548BCE9A6BBE9EF', 'A0100', 'scott100', 25, '2018-05-26 17:46:02', 5000.00, 'scott', '2018-05-26 17:46:02', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('E676E3D5BCF34A8FA057D515F84CEEAC', '200', 'scott', 20, '2018-05-28 15:56:56', 88888.00, 'scott', '2018-05-28 15:56:56', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('f1e715b4f8a24ce0a232f62b7bdb9fb5', '200', 'scott', 20, '2021-07-26 21:57:56', 88888.00, 'scott', '2021-07-26 21:57:56', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('F22B3B5F371E49918B061442010F1C9A', 'A001', 'scott', 25, '2017-08-18 14:48:11', 5000.00, 'scott', '2017-08-18 14:48:11', NULL, NULL);
INSERT INTO `employee_copy1` VALUES ('F708E15CF2904F6286EF4A72B9E60AED', '200', 'scott', 20, '2017-08-18 14:47:10', 88888.00, 'scott', '2017-08-18 14:47:10', NULL, NULL);

-- ----------------------------
-- Table structure for employee_copy2
-- ----------------------------
DROP TABLE IF EXISTS `employee_copy2`;
CREATE TABLE `employee_copy2`  (
  `id` int(36) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `empno` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '雇员编号',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '雇员名',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `birthday` datetime NULL DEFAULT NULL COMMENT '生日',
  `salary` decimal(19, 2) NULL DEFAULT NULL COMMENT '工资',
  `create_by` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_date` datetime NULL DEFAULT NULL,
  `update_by` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_date` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `id`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee_copy2
-- ----------------------------
INSERT INTO `employee_copy2` VALUES (1, '200', 'scott', 20, '2021-07-26 22:01:55', 88888.00, 'scott', '2021-07-26 22:01:55', NULL, NULL);
INSERT INTO `employee_copy2` VALUES (2, '200', 'scott', 20, '2021-07-26 22:22:25', 88888.00, 'scott', '2021-07-26 22:22:25', NULL, NULL);
INSERT INTO `employee_copy2` VALUES (3, '200', 'scott', 20, '2021-07-26 22:22:33', 88888.00, 'scott', '2021-07-26 22:22:33', NULL, NULL);
INSERT INTO `employee_copy2` VALUES (4, '200', 'scott', 20, '2021-07-26 22:22:39', 88888.00, 'scott', '2021-07-26 22:22:39', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
