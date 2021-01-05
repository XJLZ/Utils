package com.zkcm.hydrobiologicasinica.common.utils;

import java.util.Random;

/**
 * @description： 生成随机验证码
 * @author     ：lwl
 * @date       ：2019/11/13 13:38
 * @version:
 */
public class MessageCodeUtil {


    /**
     * 生成纯数字的验证码
     * @param length 验证码长度
     *result:
     *creater: lwl
     *time: 2019/11/13 13:54
     */
    public static String reandomCode(int length){
        StringBuffer code = new StringBuffer();
        Random random = new Random();
        for (int i=0;i<length;i++){
            int val = random.nextInt(10);
            code.append(val);
        }
        return code.toString();
    }


}
