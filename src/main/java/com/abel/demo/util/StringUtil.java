package com.abel.demo.util;

/**
 * @description: StringUtil
 * @author: liuzijian
 * @create: 2018-11-10 23:21
 **/
public class StringUtil {
    public static boolean isEmpty(String param){
        return param==null || "".equals(param);
    }

    public static String lowerFirstLetter(String param){
        char firstLetter = param.charAt(0);
        if(firstLetter>=65 && firstLetter<=90){
            firstLetter +=32;
        }
        return firstLetter+param.substring(1);
    }
}
