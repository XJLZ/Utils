package com.zkcm.hydrobiologicasinica.common.utils;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.zkcm.hydrobiologicasinica.common.domain.HsConstant;
import com.zkcm.hydrobiologicasinica.common.domain.QueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zkcm.hydrobiologicasinica.system.model.AuthorizeFile.AuthorizeFileVO;
import com.zkcm.hydrobiologicasinica.system.model.AuthorizeFile.SysInfoVo;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 处理排序工具类
 */
@SuppressWarnings("unchecked")
public class SortUtil {
    /**
     * 处理排序（分页情况下） for mybatis-plus
     *
     * @param request           QueryRequest
     * @param page              Page
     * @param defaultSort       默认排序的字段
     * @param defaultOrder      默认排序规则
     * @param camelToUnderscore 是否开启驼峰转下划线
     */
    public static void handlePageSort(QueryRequest request, Page page, String defaultSort, String defaultOrder, boolean camelToUnderscore) {
        page.setCurrent(request.getPageNum());
        page.setSize(request.getPageSize());
        String sortField = request.getSortField();
        if (camelToUnderscore) {
            sortField = FebsUtil.camelToUnderscore(sortField);
            defaultSort = FebsUtil.camelToUnderscore(defaultSort);
        }
        if (StringUtils.isNotBlank(request.getSortField())
                && StringUtils.isNotBlank(request.getSortOrder())
                && !StringUtils.equalsIgnoreCase(request.getSortField(), "undefined")
                && !StringUtils.equalsIgnoreCase(request.getSortOrder(), "undefined")) {
            if (StringUtils.equals(request.getSortOrder(), HsConstant.ORDER_DESC))
                page.setDesc(sortField);
            else
                page.setAsc(sortField);
        } else {
            if (StringUtils.isNotBlank(defaultSort)) {
                if (StringUtils.equals(defaultOrder, HsConstant.ORDER_DESC))
                    page.setDesc(defaultSort);
                else
                    page.setAsc(defaultSort);
            }
        }
    }

    /**
     * 处理排序 for mybatis-plus
     *
     * @param request QueryRequest
     * @param page    Page
     */
    public static void handlePageSort(QueryRequest request, Page page) {
        handlePageSort(request, page, null, null, false);
    }

    /**
     * 处理排序 for mybatis-plus
     *
     * @param request           QueryRequest
     * @param page              Page
     * @param camelToUnderscore 是否开启驼峰转下划线
     */
    public static void handlePageSort(QueryRequest request, Page page, boolean camelToUnderscore) {
        handlePageSort(request, page, null, null, camelToUnderscore);
    }

    /**
     * 处理排序 for mybatis-plus
     *
     * @param request           QueryRequest
     * @param wrapper           wrapper
     * @param defaultSort       默认排序的字段
     * @param defaultOrder      默认排序规则
     * @param camelToUnderscore 是否开启驼峰转下划线
     */
    public static void handleWrapperSort(QueryRequest request, QueryWrapper wrapper, String defaultSort, String defaultOrder, boolean camelToUnderscore) {
        String sortField = request.getSortField();
        if (camelToUnderscore) {
            sortField = FebsUtil.camelToUnderscore(sortField);
            defaultSort = FebsUtil.camelToUnderscore(defaultSort);
        }
        if (StringUtils.isNotBlank(request.getSortField())
                && StringUtils.isNotBlank(request.getSortOrder())
                && !StringUtils.equalsIgnoreCase(request.getSortField(), "undefined")
                && !StringUtils.equalsIgnoreCase(request.getSortOrder(), "undefined")) {
            if (StringUtils.equals(request.getSortOrder(), HsConstant.ORDER_DESC))
                wrapper.orderByDesc(sortField);
            else
                wrapper.orderByAsc(sortField);
        } else {
            if (StringUtils.isNotBlank(defaultSort)) {
                if (StringUtils.equals(defaultOrder, HsConstant.ORDER_DESC))
                    wrapper.orderByDesc(defaultSort);
                else
                    wrapper.orderByAsc(defaultSort);
            }
        }
    }

    /**
     * 处理排序 for mybatis-plus
     *
     * @param request QueryRequest
     * @param wrapper wrapper
     */
    public static void handleWrapperSort(QueryRequest request, QueryWrapper wrapper) {
        handleWrapperSort(request, wrapper, null, null, false);
    }

    /**
     * 处理排序 for mybatis-plus
     *
     * @param request           QueryRequest
     * @param wrapper           wrapper
     * @param camelToUnderscore 是否开启驼峰转下划线
     */
    public static void handleWrapperSort(QueryRequest request, QueryWrapper wrapper, boolean camelToUnderscore) {
        handleWrapperSort(request, wrapper, null, null, camelToUnderscore);
    }

    /**
     * 加密之前的排序
     */
    public static String sortBeforeEncode(AuthorizeFileVO authorize, SysInfoVo sysInfo){
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("websiteId",authorize.getWebsiteId());
        map.put("branchId",authorize.getBranchId());
        map.put("orderNo",authorize.getOrderNo());
        map.put("maxDownload",authorize.getMaxDownload());
        map.put("maxRunning",authorize.getMaxRunning());
        map.put("startTime",authorize.getStartTime());
        map.put("endTime",authorize.getEndTime());
        map.put("ipRange",authorize.getIpRange());
        map.put("ipAddress", sysInfo.getIpAddress());
        map.put("macAddress", sysInfo.getMacAddress());
        map.put("cpuSerial", sysInfo.getCpuSerial());
        map.put("mainBoardSerial", sysInfo.getMainBoardSerial());
        List<Map.Entry<String, Object>> infoIds = new ArrayList<>(map.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        Collections.sort(infoIds, Comparator.comparing(o -> (o.getKey())));
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> item : infoIds) {
            if (item.getKey() != null || item.getKey() != "") {
                String key = item.getKey();
                String val = item.getValue().toString();
                if (!(val == "" || val == null)) {
                    sb.append(key + "=" + val + "&");
                }
            }
        }
        return sb.toString().substring(0,sb.toString().length()-1);
    }

    public static String sortBeforeEncode(String source){
        String[] split = source.split(StringPool.AMPERSAND);
        Map map = new HashMap<>(16);
        for (String s : split) {
            String[] kv = s.split(StringPool.EQUALS);
            map.put(kv[0],kv[1]);
        }
        List<Map.Entry<String, Object>> infoIds = new ArrayList<>(map.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        Collections.sort(infoIds, Comparator.comparing(o -> (o.getKey())));
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> item : infoIds) {
            if (item.getKey() != null || item.getKey() != "") {
                String key = item.getKey();
                String val = item.getValue().toString();
                if (!(val == "" || val == null)) {
                    sb.append(key + "=" + val + "&");
                }
            }
        }
        return sb.toString().substring(0,sb.toString().length()-1);
    }
}
