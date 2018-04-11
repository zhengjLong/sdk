package com.library.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @Author: jerome
 * @Date: 2017-09-20
 */
@SuppressWarnings("WrongConstant")
public class DateUtil {

    public static final String yyMMdd = "yy年MM月dd日";
    public static final String yyyyMMdd = "yyyy年MM月dd日";
    public static final String HHmmss = "HH:mm:ss";
    public static final String HHmm = "HH:mm";
    public static final String yyMMddHHmmss = "yy年MM月dd日 HH:mm:ss";
    public static final String yyyyMMddHHmm = "yyyy年MM月dd日 HH:mm";
    public static final String yyyyMMddHHmmss = "yyyy年MM月dd日 HH:mm:ss";
    public static final String MMddHHmm = "MM月dd日 hh:mm aa";

    public static final String MMdd = "MM月dd日";
    public static final String yyyyMM = "yyyy年MM";

    public static final String yyyymm = "yyyy年MM";
    public static final String yyyymmdd = "yyyy年MM月dd日";

    public static final String yyyy_MM_dd = "yyyy-MM-dd";

    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    public static final String MM_dd = "MM-dd";

    public static final String MM_dd_HH_mm = "MM-dd HH:mm";

    public static final String yyyy_MMddHHmm = "yyyy-MM-dd HH:mm";
    public static final String yyyy_MMddHHmmss = "yyyy-MM-dd HH:mm:ss";


    /**
     * 是否当天时间
     * @param var0
     * @return
     */
    public static boolean isSameDay(long var0) {
        TimeInfo var2 = getTodayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    /**
     * 是否是昨天
     * @param var0
     * @return
     */
    private static boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    /**
     * 显示时间的格式 ：
     * @param var0
     * @return
     */
    public static String getTime(Date var0) {
        String var1 = null;
        long var2 = var0.getTime();
        if(isSameDay(var2)) {
            var1 = HHmm;
        } else if(isYesterday(var2)) {
            var1 = "M月d日";
        } else {
            var1 = "M月d日";
        }
        return (new SimpleDateFormat(var1, Locale.CHINA)).format(var0);
    }

    public static String parseToString(long curentTime, String style) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        return formatter.format(now.getTime());
    }

    public static long parse2Long(String s, String style) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(style);
        Date date = null;
        if (s == null || s.length() < 5)
            return 0;
        try {
            date = simpleDateFormat.parse(s);
            return date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 计算时间戳与当前时间的差距
     * @param aTimeStamp
     * @return
     */
    public static String getTimeDistance(long aTimeStamp) {
        String ret = null;
        long currenttime = new Date().getTime();
        long offset = currenttime - aTimeStamp;
        offset /= 1000;
        if (aTimeStamp == 0) {
            ret = "";
        } else {
            if (offset > 0) {
                if (offset / 60 == 0) {
//                    ret = offset % 60 + "秒前";
                    ret = "1分钟前";
                } else if (offset / 60 / 60 == 0) {
                    ret = offset / 60 % 60 + "分钟前";
                } else if (offset / 60 / 60 / 24 == 0) {
                    ret = offset / 60 / 60 % 24 + "小时前";
                } else {
                    ret = DateUtil.parseToMD(aTimeStamp);
                }
            } else {
                ret = "刚刚";
            }
        }
        return ret;
    }

    public static boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if(var4 < 0L) {
            var4 = -var4;
        }

        return var4 < 30000L;
    }

    public static String parseToMD(long time) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(time);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String str = formatter.format(now.getTime());
        return str;
    }

    public static  class TimeInfo {
        private long startTime;
        private long endTime;

        public TimeInfo() {
        }

        public long getStartTime() {
            return this.startTime;
        }

        public void setStartTime(long var1) {
            this.startTime = var1;
        }

        public long getEndTime() {
            return this.endTime;
        }

        public void setEndTime(long var1) {
            this.endTime = var1;
        }
    }

    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.add(5, -1);
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.add(5, -1);
        var4.set(11, 23);
        var4.set(12, 59);
        var4.set(13, 59);
        var4.set(14, 999);
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }



    public static TimeInfo getTodayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.set(11, 0);
        var0.set(12, 0);
        var0.set(13, 0);
        var0.set(14, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
        Calendar var5 = Calendar.getInstance();
        var5.set(11, 23);
        var5.set(12, 59);
        var5.set(13, 59);
        var5.set(14, 999);
        Date var6 = var5.getTime();
        long var7 = var6.getTime();
        TimeInfo var9 = new TimeInfo();
        var9.setStartTime(var2);
        var9.setEndTime(var7);
        return var9;
    }

}