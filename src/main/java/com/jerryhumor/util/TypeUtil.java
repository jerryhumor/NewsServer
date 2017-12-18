package com.jerryhumor.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeUtil {

    /**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static boolean isPercentage(String str){
        Pattern pattern = Pattern.compile("[0-9]*%");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static boolean isDecimal(String str){
        Pattern pattern = Pattern.compile("[0-9]+.[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str){
        return isNumber(str) || isPercentage(str) || isDecimal(str);
    }
}
