package com.trotyzyq.util;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by trotyzyq on 2018/8/13.
 */

public class TimeUtil {

    /**获取当前时间字符串**/
    public static String getCurrentTimeString(){
        return stampToDate(System.currentTimeMillis()/1000);
    }

    /** 获取当前日期**/
    public static String getNowDate(){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }

    /**时间戳秒数转时间**/
    public static String stampToDate(long time){
        String s=String.valueOf(time*1000);
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
}
