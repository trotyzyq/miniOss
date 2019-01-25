package com.trotyzyq.util;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * 字符串操作
 * @author gql
 *
 * @author whx
 * @date 2018-10-22 更新
 */
public class StringUtil {

    /**
     * 处理页面传递的特殊字符，将<>()&;:/\'"替换为" "
     *
     * @param source 处理前的字符串
     * @return 处理后的字符串
     */
    public static String rightfulString(String source) {
        if (isNull(source)) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        int sourceLength = source.length();
        for (int i = 0; i < sourceLength; i++) {
            char c = source.charAt(i);
            switch (c) {
                case '<':
                    buffer.append(" ");
                    break;
                case '>':
                    buffer.append(" ");
                    break;
                case '(':
                    buffer.append(" ");
                    break;
                case ')':
                    buffer.append(" ");
                    break;
                case '&':
                    buffer.append(" ");
                    break;
                case ':':
                    buffer.append(" ");
                    break;
                case ';':
                    buffer.append(" ");
                    break;
                case '\'':
                    buffer.append(" ");
                    break;
                case '\"':
                    buffer.append(" ");
                    break;
                case '\\':
                    buffer.append(" ");
                    break;
                case '/':
                    buffer.append(" ");
                    break;
                case '*':
                    buffer.append(" ");
                    break;
                default:
                    buffer.append(c);
            }
        }
        return buffer.toString();
    }

    /**
     * 对字符串进行处理，把'null'、'NULL'处理成''空字符串，非空的字符串会执行trim
     * @param paramStr  待处理的字符串
     * @return  处理后的字符串
     */
    public static String stringNullHandle(String paramStr){
        if(null == paramStr || paramStr.isEmpty() || "".equals(paramStr)){
            return "";
        }
        return paramStr.trim();
    }

    /**
     * 字符串非空判断
     * 属于空的：（NULL，‘’， ‘null’,'NULL'）
     * @param paramStr  待判断的字符串
     * @return  字符串为空返回：true，字符串不为空返回：false
     */
    public static boolean isNull(String paramStr){
        if(null == paramStr || paramStr.isEmpty()){
            return true;
        }

        paramStr = paramStr.trim();
        return "".equals(paramStr) || "null".equalsIgnoreCase(paramStr);
    }

    /**
     * 字符串为空判断
     * 属于空的：（NULL，‘’， ‘null’,'NULL'）
     * @param paramStr  待判断的字符串
     * @return  字符串为空返回：false，字符串不为空返回：true
     */
    public static boolean isNotNull(String paramStr){
        return !isNull(paramStr);
    }

    /**
     * 判断字符串中是否存在空白字符(只处理UTF-8字符集的字符串)
     * 空格符(0x0020)，英文下ASCII扩充的UNICODE空格
     * @param paramStr 待判断字符串
     * @return 存在空白字符：返回true，不存在空白字符：返回false
     */
    public static boolean isBlank(String paramStr) {
        //非UTF-8直接返回true
        if (!"UTF-8".equals(Charset.defaultCharset().toString())) {
            return true;
        }

        int strLen;
        if (paramStr == null || (strLen = paramStr.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (paramStr.charAt(i) == 0x0020) {
                return true;
            }
        }
        return false;
    }



    /**
     * 去除字符串中的 <script></script>
     * @param value 处理前的字符串
     * @return 处理后的字符串
     */
    public static String removeJavaScript(String value) {
        return Pattern.compile("<script[\\s\\S]*?>[\\s\\S]*?<\\/script>",
                Pattern.CASE_INSENSITIVE).matcher(value).replaceAll("");
    }
}
