package com.trotyzyq;


import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by trotyzyq on 2018/8/13.
 */

public class TimeUtil {
    static Logger logger= Logger.getLogger(TimeUtil.class);

    /**获取当前时间戳,以秒为单位**/
    public static long getCurrentTime(){
        return System.currentTimeMillis()/1000;
    }
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


    /**将时间(字符串)转换为时间戳(字符串)**/
    public static String dateToStamp(String dateTime)  {
        return String.valueOf(dateToStampWithLong(dateTime)/1000);
    }

    /**将时间(字符串)转换为时间戳(字符串)**/
    public static long dateToStampWithLong(String dateTime)  {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime()/1000;
        return ts;
    }

    /**获取当前时间的小时数**/
    public static Integer getCurrentTimeHour( )  {
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=df.format(day);
        int hour=Integer.parseInt(time.substring(11,13));
        return hour;
    }

    /**获取当前小时时间的下一个小时**/
    public static Integer getCurrentTimeNextHour( )  {
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=df.format(day);
        int hour=Integer.parseInt(time.substring(11,13));
        return hour+1;
    }

    /**获取当前小时时间的下一个小时的时间戳**/
    public static Long getCurrentTimeNextHourStamp( )  {
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=df.format(day);
        int hour=Integer.parseInt(time.substring(11,13));
        String before = time.substring(0,11);
        String after = ":00:00";
        return dateToStampWithLong(before+(hour+1+"")+after);
    }

    /**获取当前小时时间的指定小时的时间戳**/
    public static Long getCurrentTimeDecideHourStamp(int h){
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=df.format(day);
        int hour=Integer.parseInt(time.substring(11,13));
        String before = time.substring(0,11);
        String after = ":00:00";
        if(hour<=h){
            return dateToStampWithLong(before+(h+"")+after);
        }else {
            return dateToStampWithLong(before+(hour+"")+after)+h+(24-hour)*3600;
        }

    }
    public static void main(String[] args) {
        long cur=getCurrentTime()*1000;
        System.out.println(cur);
        System.out.println(getCurrentTimeHour());
        System.out.println(getCurrentTimeNextHourStamp());
    }
}
