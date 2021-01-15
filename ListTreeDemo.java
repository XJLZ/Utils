package com.example.demo.vo;


import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 评论列表
 */
public class ReplyVO {
    private Integer id;
    private Integer comId;
    private Integer replyId;
    private Integer userId;
    private String userName;
    private String msg;
    private String updateTime;
    private List<ReplyVO> subList;

    public List<ReplyVO> getSubList() {
        return subList;
    }

    public void setSubList(List<ReplyVO> subList) {
        this.subList = subList;
    }

    public Integer getReplyId() {
        return replyId;
    }

    public void setReplyId(Integer replyId) {
        this.replyId = replyId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getComId() {
        return comId;
    }

    public void setComId(Integer comId) {
        this.comId = comId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }


    public List queryReplyList(int comId) {
        List<ReplyVO> list = commonMapper.queryReplyList(comId);
        List<ReplyVO> replyList = list.stream().filter(item -> StringUtils.isEmpty(item.getReplyId())).collect(Collectors.toList());
        List<ReplyVO> list2 = list.stream().filter(item -> !StringUtils.isEmpty(item.getReplyId())).collect(Collectors.toList());
        return getList(replyList, list2);
    }

    public List<ReplyVO> getList(List<ReplyVO> replyList, List<ReplyVO> list2) {
        List<ReplyVO> resList = new ArrayList<>();
        replyList.forEach(r -> {
            List<ReplyVO> firstList = list2.stream().filter(vo -> r.getId().equals(vo.getReplyId()))
                    .sorted(Comparator.comparing(ReplyVO::getUpdateTime).reversed()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(firstList)) {
                r.setSubList(getList(firstList, list2));
            }else{
                r.setSubList(firstList);
            }
            resList.add(r);
        });
        return resList;

    }
}


/*


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for reply_info
-- ----------------------------
DROP TABLE IF EXISTS `reply_info`;
CREATE TABLE `reply_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NULL DEFAULT NULL,
  `comId` int(11) NULL DEFAULT NULL,
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `updatetime` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `userName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `replyId` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reply_info
-- ----------------------------
INSERT INTO `reply_info` VALUES (1, 2, 1, '6', '2020-12-20 22:28:18', '评论人1', NULL);
INSERT INTO `reply_info` VALUES (3, 2, 1, '666', '2020-12-30 22:28:19', '评论人1', 2);
INSERT INTO `reply_info` VALUES (5, 3, 1, '8', '2020-12-20 22:28:29', '评论人2', NULL);
INSERT INTO `reply_info` VALUES (7, 2, 1, '7', '2020-12-20 23:39:19', '评论人1', NULL);
INSERT INTO `reply_info` VALUES (8, 3, 1, '88', '2020-12-30 23:40:12', '评论人2', 7);
INSERT INTO `reply_info` VALUES (10, 3, 1, '333', '2021-01-15 14:04:10', '33333', NULL);
INSERT INTO `reply_info` VALUES (11, 3, 1, '8888', '2020-12-31 20:28:21', '评论人2', 10);
INSERT INTO `reply_info` VALUES (12, 1, 1, '88888', '2020-12-31 20:28:35', '发表人', 11);
INSERT INTO `reply_info` VALUES (13, 3, 1, '888888', '2020-12-31 20:28:48', '评论人2', 12);
INSERT INTO `reply_info` VALUES (14, 1, 1, '8888888', '2020-12-31 20:29:06', '发表人', 13);
INSERT INTO `reply_info` VALUES (15, 3, 1, '88888888', '2020-12-31 20:29:22', '评论人2', 14);
INSERT INTO `reply_info` VALUES (17, 4, 1, '4', '2020-12-31 22:08:02', '评论人4', NULL);
INSERT INTO `reply_info` VALUES (19, 5, 1, '5', '2020-12-31 22:08:02', '评论人5', NULL);
INSERT INTO `reply_info` VALUES (21, 6, 1, '6', '2020-12-31 22:08:02', '评论人6', NULL);
INSERT INTO `reply_info` VALUES (23, 7, 1, '7', '2020-12-31 22:08:02', '评论人7', NULL);
INSERT INTO `reply_info` VALUES (24, 1, 1, '77', '2020-12-31 22:08:02', '发表人', 23);
INSERT INTO `reply_info` VALUES (25, 8, 1, '8', '2020-12-31 22:08:02', '评论人8', NULL);
INSERT INTO `reply_info` VALUES (26, 1, 1, '88', '2020-12-31 22:08:02', '发表人', 25);
INSERT INTO `reply_info` VALUES (27, 9, 1, '9', '2020-12-31 22:08:02', '评论人9', NULL);
INSERT INTO `reply_info` VALUES (29, 10, 1, '10', '2020-12-31 22:08:02', '评论人10', NULL);
INSERT INTO `reply_info` VALUES (31, 11, 1, '11', '2020-12-31 22:08:02', '评论人11', NULL);
INSERT INTO `reply_info` VALUES (33, 33, 1, '231232', '2021-01-15 11:20:12', '44444', 24);
INSERT INTO `reply_info` VALUES (34, 23, 1, '请问请问', '2021-01-15 14:26:40', '3444', 24);

SET FOREIGN_KEY_CHECKS = 1;



*/