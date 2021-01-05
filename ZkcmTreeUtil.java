package com.zkcm.hydrobiologicasinica.common.utils;

import com.zkcm.hydrobiologicasinica.common.domain.HsConstant;
import com.zkcm.hydrobiologicasinica.common.domain.Tree;
import freemarker.template.utility.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：Lidl
 * @description：树工具类
 * @date ：2019/11/1
 */
public class ZkcmTreeUtil {


    /**
     * * 解析树形数据
     *
     * @param topId
     * @param treeList
     * @return
     */
    public static  List<Tree> getTreeList(String topId, List<Tree> treeList) {
        List<Tree> resultList = new ArrayList<>();
        // 获取顶层元素集合
        String parentId;
        for (Tree t : treeList) {
            parentId = t.getParentId();
            if (HsConstant.ROOT_NODE_ID.equals(parentId) || ZkcmStringUtil.isEmpty(parentId)
                    || topId.equals(parentId)) {
                resultList.add(t);
            }
        }
        // 获取每个顶层元素的子数据集合
        for (Tree t : resultList) {
            t.setChildren(getSubList(t.getKey(), treeList));
        }
        return resultList;
    }

    /**
     * * 获取子数据集合
     *
     * @param id
     * @param entityList
     * @return
     */
    private static  List<Tree> getSubList(String id, List<Tree> entityList) {
        List<Tree> childList = new ArrayList<>();
        String parentId;
        // 子集的直接子对象
        for (Tree t : entityList) {
            parentId = t.getParentId();
            if (id.equals(parentId)) {
                childList.add(t);
            }
        }
        // 子集的间接子对象
        for (Tree t : childList) {
            t.setChildren(getSubList(t.getKey(), entityList));
        }
        // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

    /**
     * * 获取子数据集合
     *
     * @param id
     * @param treeList
     * @return
     */
    public static void getSubIdList(String id, List<Tree> treeList,List<String> childrenIdList) {
        List<Tree> childList = new ArrayList<>();
        String parentId;
        // 子集的直接子对象
        for (Tree t : treeList) {
            parentId = t.getParentId();
            if (id.equals(parentId)) {
                childList.add(t);
                childrenIdList.add(t.getId());
            }
        }
        // 递归退出条件
        if (childList.size() == 0) {
            return;
        }
        // 子集的间接子对象
        for (Tree t : childList) {
            getSubIdList(t.getId(), treeList,childrenIdList);
        }

    }

}
