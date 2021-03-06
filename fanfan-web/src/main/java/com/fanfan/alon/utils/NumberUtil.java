package com.fanfan.alon.utils;

import java.util.Random;

/**
 * 功能描述:随机数生成类
 * @param:
 * @return:
 * @auther: zoujiulong
 * @date: 2018/9/3   18:15
 */
public class NumberUtil {

    public static String getRandomString(int length) { // length表示生成字符串的长度
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static int getRandomInt(int length) { // length表示生成字符串的长度
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return Integer.parseInt(sb.toString());
    }
}
