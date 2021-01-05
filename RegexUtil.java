package com.zkcm.hydrobiologicasinica.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    // html img 标签 src 内容
    private final static String HTML_IMG_SRC = "<(img|IMG)(.*?)(/>|></img>|>)";

    /**
     * 获取html img 标签 src 内容
     *
     * @param content
     * @return
     */
    public static List<String> getHtmlImgSrc(String content) {
        List<String> list = new ArrayList<String>();
        Pattern imgPattern = Pattern.compile("<(img|IMG)(.*?)(/>|></img>|>)");
        Matcher imgMatcher = imgPattern.matcher(content);
        boolean imgMatchResult = imgMatcher.find();
        if (imgMatchResult) {
            while (imgMatchResult) {
                String imgTarget = imgMatcher.group(2);
                Pattern srcPattern = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                Matcher srcMatcher = srcPattern.matcher(imgTarget);
                if (srcMatcher.find()) {
                    String srcTarget = srcMatcher.group(3);
                    list.add(srcTarget);
                }

                imgMatchResult = imgMatcher.find();
            }
        }
        return list;
    }
}
