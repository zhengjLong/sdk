package com.library.base.utils;

import android.util.Patterns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author: jerome
 * @Date: 2017-09-20
 */
public class CheckUtil {


    /**
     * 判是否是字母
     */
    public static boolean isLetter(String txt) {
        if (isNull(txt)) {
            return false;
        }
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m = p.matcher(txt);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否全部由字母和数字下划线组成
     *
     * @param name
     * @return
     */
    public static boolean isLimit(String name) {
        // ^.[a-zA-Z]\w{m,n}$ 由m-n位的字母数字或下划线组成
        Pattern p = Pattern.compile("[a-zA-Z0-9_]*");
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * 隐藏手机号码中间四位
     * @param phone 13711112222
     * @return 137****2222
     */
    public static String getPhoneHide(String phone) {
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static boolean isMobile(String mobile) {
        boolean isValid = false;
        String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
        String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
        CharSequence inputStr = mobile + "";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        Pattern pattern2 = Pattern.compile(expression2);
        Matcher matcher2 = pattern2.matcher(inputStr);
        if (matcher.matches() || matcher2.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 判断是否是联系方式(手机号码+固话)
     * @param mobiles
     * @return
     */
    public static boolean isPhoneNumber(String mobiles) {
        if (isMobile(mobiles) || isTelNumber(mobiles)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否是固定电话号码
     * @param telPhone
     * @return
     */
    public static boolean isTelNumber(String telPhone) {
        Pattern p = Pattern.compile("(\\d{3,4}-)\\d{6,8}");
        Matcher m = p.matcher(telPhone);
        return m.matches();
    }

    /**
     * 判断是否为身份证号码
     */
    public static boolean isIDCard(String idCard) {
        if (isIDCard15(idCard) || isIDCard18(idCard)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否是15位身份证号码
     *
     * @return
     */
    public static boolean isIDCard15(String idCard) {
        // ^.[a-zA-Z]\w{m,n}$ 由m-n位的字母数字或下划线组成
        Pattern p = Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$");
        Matcher m = p.matcher(idCard);
        return m.matches();
    }

    /**
     * 判断是否是18位身份证号码
     *
     * @return
     */
    public static boolean isIDCard18(String idCard) {
        // ^.[a-zA-Z]\w{m,n}$ 由m-n位的字母数字或下划线组成
        Pattern p = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$");
        Matcher m = p.matcher(idCard);
        return m.matches();
    }


    /**
     * 判断对象是否为空
     */
    public static boolean isNull(Object strObj) {
        String str = strObj + "";
        if (!"".equals(str) && !"null".equals(str)) {
            return false;
        }
        return true;
    }

    /**
     * 判断邮编
     * @return
     */
    public static boolean isZipNO(String zipString){
        String str = "^[1-9][0-9]{5}$";
        return Pattern.compile(str).matcher(zipString).matches();
    }

    /**
     * 判断是否邮箱
     */
    public static boolean checkEmail(Object strObj) {
        String str = strObj + "";
        if (!str.endsWith(".com")) {
            return false;
        }
        String match = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern pattern = Pattern.compile(match);
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * string 转 int 可接收double型
     * @param num
     * @return
     */
    public static int getInt(String num){
        int number = 0;
        try {
            number = Double.valueOf(num).intValue();
        }catch (Exception e){
            e.printStackTrace();
        }
        return number;
    }

    /**
     * 格式化double成两位小数
     * @param number  String或者double
     * @return
     */
    public static String formatDouble(Object number){
        try{
            java.text.DecimalFormat   df= new java.text.DecimalFormat("0.00");
            return df.format(number);
        }catch (Exception e){
            Logcat.INSTANCE.e("格式化出错：" + e.getMessage());
        }
        return "0.00";
    }

    /**
     * 是否是一个完整的url
     *
     * @param url
     * @return
     */
    public static boolean isWebUrl(String url) {
        if (url == null)
            return false;
        return Patterns.WEB_URL.matcher(url).matches();
    }

    /**
     * 是否是一个完整的图片url
     *
     * @param url
     * @return
     */
    public static boolean isImgUrl(String url) {
        String pattern = "^((http://)|(https://)|(ftp://)).*?.(png|jpg|jpeg|gif|bmp)";

        return Pattern.matches(pattern, url);
    }

    /**
     * 是否为银行卡
     * @param mobiles
     * @return
     */
    public static boolean isBankCard(String mobiles) {
        Pattern p = Pattern.compile("^(\\d{16}|\\d{19})$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /**
     * 检查是否为数字，以及这个数在min与max之间，包含min与max
     */
    public static boolean checkNum(Object strObj, int min, int max) {
        String str = strObj + "";
        try {
            int number = getInt(str);
            if (number <= max && number >= min) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 压缩字符串到Zip
     *
     * @param str
     * @return 压缩后字符串
     * @throws IOException
     */
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        return out.toString("ISO-8859-1");
    }

    /**
     * 解压Zip字符串
     *
     * @param str
     * @return 解压后字符串
     * @throws IOException
     */
    public static String uncompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayInputStream in = new ByteArrayInputStream(str
                .getBytes("UTF-8"));
        return uncompress(in);
    }

    /**
     * 解压Zip字符串
     *
     * @param inputStream
     * @return 解压后字符串
     * @throws IOException
     */
    public static String uncompress(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPInputStream gunzip = new GZIPInputStream(inputStream);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toString();
    }

    /**
     * 判定输入汉字，通过Unicode表
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否中文
     * @param str
     * @return
     */
    public static boolean isChinese(String str){
        String reg = "[\\u4e00-\\u9fa5]+";//+表示一个或多个中文，
        boolean flag= false;
        if (str.matches(reg)){
            flag = true;
        }
        return flag;
    }

}

