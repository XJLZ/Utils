package com.zkcm.hydrobiologicasinica.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : tx
 * @date : 2019/11/8 9:40
 * 汉字转拼音工具类
 */
public class CharacterChangeUtil {
    /**
     * @author : tx
     * @date : 2019/11/8 9:42
     * 取第一个汉字的首字母
     */
    public static String getFirstLetter(String ChineseLanguage) {
        if(StringUtils.isBlank(ChineseLanguage)){
            return "";
        }
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 输出拼音全部大写
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        // 不带声调
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        try {
            String str = String.valueOf(cl_chars[0]);
            // 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
            if (str.matches("[\\u4e00-\\u9fa5]+")) {
                hanyupinyin = PinyinHelper.toHanyuPinyinStringArray(
                        cl_chars[0], defaultFormat)[0].substring(0, 1);
                ;
            } /*else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
                hanyupinyin += cl_chars[0];
            } else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母

                hanyupinyin += cl_chars[0];
            } else {// 否则不转换

            }*/
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            System.out.println("字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }

    public static String getPinYin(String string){
        char[] hz = null;
        //该方法的作用是返回一个字符数组，该字符数组中存放了当前字符串中的所有字符
        hz = string.toCharArray();
        //该数组用来存储
        String[] py = new String[hz.length];
        //设置汉子拼音输出的格式
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        //存放拼音字符串
        String pys = "";
        int len = hz.length;
        try {
            for (int i = 0; i < len ; i++ ){
                //先判断是否为汉字字符
                if(Character.toString(hz[i]).matches("[\\u4E00-\\u9FA5]+")){
                    //将汉字的几种全拼都存到py数组中
                    py = PinyinHelper.toHanyuPinyinStringArray(hz[i],format);
                    //取出改汉字全拼的第一种读音，并存放到字符串pys后
                    pys += py[0];
                }else{
                    //如果不是汉字字符，间接取出字符并连接到 pys 后
                    pys += Character.toString(hz[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e){
            e.printStackTrace();
        }
        return pys;
    }

    public static String getPinYinWithChar(char c){
        //该方法的作用是返回一个字符数组，该字符数组中存放了当前字符串中的所有字符
        //该数组用来存储
        String[] py;
        //设置汉子拼音输出的格式
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        //存放拼音字符串
        String pys = "";
        try {
            if(Character.toString(c).matches("[\\u4E00-\\u9FA5]+")){
                //将汉字的几种全拼都存到py数组中
                py = PinyinHelper.toHanyuPinyinStringArray(c,format);
                //取出改汉字全拼的第一种读音，并存放到字符串pys后
                pys += py[0];
            }else{
                //如果不是汉字字符，间接取出字符并连接到 pys 后
                pys += Character.toString(c);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e){
            e.printStackTrace();
        }
        return pys;
    }

    public static String getFirstChar(String str){
        int n;
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            n = (int)c;
            if((19968 <= n && n <40869)) {
                String pinYinWithOne = getPinYinWithChar(c);
                result.append(pinYinWithOne.charAt(0));
            }else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String str = "中国古生物2312zxczc地层xc知识库11";
        int n;
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            n = (int)c;
            if((19968 <= n && n <40869)) {
                String pinYinWithOne = CharacterChangeUtil.getPinYinWithOne(c);
                result.append(pinYinWithOne.charAt(0));
            }else {
                result.append(c);
            }
        }
        System.out.println(result.toString());

        String pinYin = CharacterChangeUtil.getPinYin("李四");
        System.out.println(pinYin);

        System.out.println(hasChinese("77iuay"));
    }

     public static boolean hasChinese(String str){
        int n;
        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            n = (int) c;
            if ((19968 <= n && n < 40869)) {
                return true;
            }
        }
        return false;
    }
}
