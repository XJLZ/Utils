package com.example.demo.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: XJL
 * @Description:
 * @create 2021-12-16 13:58
 * @Modified By:
 */
public class Bv2AvUtil {
    public static String[] table = {"f", "Z", "o", "d", "R", "9", "X", "Q", "D", "S", "U", "m", "2", "1", "y", "C", "k", "r", "6", "z", "B", "q", "i", "v", "e", "Y", "a", "h", "8", "b", "t", "4", "x", "s", "W", "p", "H", "n", "J", "E", "7", "j", "L", "5", "V", "G", "3", "g", "u", "M", "T", "K", "N", "P", "A", "w", "c", "F"};
    public static String tableStr = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
    public static int[] s = {11, 10, 3, 8, 4, 6};
    public static long xor = 177451812L;
    public static long add = 8728348608L;
    public static Pattern pattern = Pattern.compile("[Bb][Vv][fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF]{10}");

    public static long change2av(String bv) {
        String str;
        if (bv.length() == 12) {
            str = bv;
        } else if (bv.length() == 10) {
            str = "BV" + bv;
            // 根据官方 API，BV 号开头的 BV1 其实可以省略
            // 不过单独省略个 B 又不行（
        } else if (bv.length() == 9) {
            str = "BV1" + bv;
        } else {
            return -1L;
        }
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            return -1L;
        }
        char[] chars = str.toCharArray();
        long result = 0;
        int i = 0;
        while (i < 6) {
            result += tableStr.indexOf(chars[s[i]]) * Math.pow(58, i);
            i += 1;
        }
        return result - add ^ xor;
    }

    public static String change2bv(long av) {
        long num;
        if (av <= 0) {
            return "error";
        }
        num = av;
        num = (num ^ xor) + add;
        String[] result = {"b", "v", "1", " ", " ", "4", " ", "1", " ", "7", " ", " "};
        int i = 0;
        while (i < 6) {
            // 这里改写差点犯了运算符优先级的坑
            // 果然 Python 也不是特别熟练
            // 说起来 ** 按照传统语法应该写成 Math.pow()，但是我个人更喜欢 ** 一些
            result[s[i]] = table[(int) (Math.floor(num / Math.pow(58, i)) % 58)];
            i += 1;
        }
        return StringUtils.join(result);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(change2bv(675656900));
        long aid = change2av("bv14U4y1A7Uu");
        String url = "https://api.bilibili.com/x/web-interface/view?aid=" + aid;
        String host = "https://api.bilibili.com";
        HttpResponse response = HttpUtils.doGet(host, url);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }
}
