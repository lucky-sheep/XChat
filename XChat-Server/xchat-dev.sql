/*
SQLyog Professional v12.09 (64 bit)
MySQL - 8.0.28 : Database - xchat-dev
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`xchat-dev` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `xchat-dev`;

/*Table structure for table `chat_msg` */

DROP TABLE IF EXISTS `chat_msg`;

CREATE TABLE `chat_msg` (
  `id` varchar(64) NOT NULL,
  `send_user_id` varchar(64) NOT NULL,
  `accept_user_id` varchar(64) NOT NULL,
  `msg` varchar(255) NOT NULL,
  `sign_flag` int NOT NULL COMMENT '是否已读',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `chat_msg` */

/*Table structure for table `friend_request` */

DROP TABLE IF EXISTS `friend_request`;

CREATE TABLE `friend_request` (
  `id` varchar(64) NOT NULL,
  `send_user_id` varchar(64) DEFAULT NULL,
  `accept_user_id` varchar(64) DEFAULT NULL,
  `request_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='发送好友请求的表';

/*Data for the table `friend_request` */

insert  into `friend_request`(`id`,`send_user_id`,`accept_user_id`,`request_datetime`) values ('211419008','210507275','211331714','2022-02-20 16:31:01');

/*Table structure for table `my_friends` */

DROP TABLE IF EXISTS `my_friends`;

CREATE TABLE `my_friends` (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `my_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `my_friend_id` varchar(64) NOT NULL,
  `notes` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT KEY `myuserid_index` (`my_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `my_friends` */

insert  into `my_friends`(`id`,`my_user_id`,`my_friend_id`,`notes`) values ('211302712','210507275','211331712','BBQ'),('211302713','210507275','211331713',NULL),('211331714','211331712','210507275','老李'),('211331715','211331713','210507275',NULL);

/*Table structure for table `user_info` */

DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
  `id` varchar(64) NOT NULL COMMENT '用户ID',
  `username` varchar(64) NOT NULL,
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(64) NOT NULL,
  `client_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `face_image` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `face_image_big` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `nickname` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `qrcode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_index` (`email`),
  UNIQUE KEY `username_index` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `user_info` */

insert  into `user_info`(`id`,`username`,`email`,`password`,`client_id`,`face_image`,`face_image_big`,`nickname`,`qrcode`) values ('210507275','邹长林','zchanglin@aliyun.com','e10adc3949ba59abbe56e057f20f883e','106306a1ba1320106fc03ddcd80be98b',NULL,NULL,NULL,NULL),('211331712','张三','zhangsan@aliyun.com','e10adc3949ba59abbe56e057f20f883e','a6dbf34690b693bfd2b8612d769fd8b2','https://zouchanglin.cn/images/avatar.png',NULL,NULL,NULL),('211331713','李四','lisi@aliyun.com','e10adc3949ba59abbe56e057f20f883e','a6dbf34690b693bfd2b8612d769fd8b2',NULL,NULL,NULL,NULL),('211331714','小可爱','lilililala@qq.com','e10adc3949ba59abbe56e057f20f883e','a6dbf34690b693bfd2b8612d769fd8b2','https://zouchanglin.cn/images/avatar.png',NULL,NULL,NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
