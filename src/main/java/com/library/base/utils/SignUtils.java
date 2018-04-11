package com.library.base.utils;

import android.net.Uri;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 签名算法
 *
 * @Author: jerome
 * @Date: 2017-09-20
 */
public class SignUtils {

    public static final String KEY_MAC_HMAC_MD5 = "HmacMD5";
    public static final String KEY_MAC_HMAC_SHA1 = "HmacSHA1";
    public static final String KEY_MAC_HMAC_SHA256 = "HmacSHA256";
    public static final String KEY_MAC_HMAC_SHA384 = "HmacSHA384";
    public static final String KEY_MAC_HMAC_SHA512 = "HmacSHA512";
    // DES3默认密钥
    private final static String secretKey = "xclsylgqqcdyqxdgdsylwjdn";//24位
    // DES3默认向量
    private final static String iv = "wqnmlgba";//8位
    // 加解密统一使用的编码方式
    private final static String encoding = "utf-8";

    private static final String ALGORITHM = "RSA";

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    private static final String DEFAULT_CHARSET = "UTF-8";

    // 默认HMAC 算法
    private static final String DEFAULT_HMAC = KEY_MAC_HMAC_SHA1;


    /**
     * byte to hex
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * HMAC加密
     *
     * @param KEY_MAC 算法类型，参考常量KEY_MAC_*
     * @param key     私钥
     * @param content 加密内容
     * @return
     * @throws Exception
     */
    public static String encryptHMAC(String KEY_MAC, String key, String content) throws Exception {
        Mac mac = Mac.getInstance(KEY_MAC);
        SecretKey secretKey = new SecretKeySpec(key.getBytes(DEFAULT_CHARSET), mac.getAlgorithm());
        mac.init(secretKey);
        byte[] data = mac.doFinal(content.getBytes(DEFAULT_CHARSET));
        return bytesToHex(data);
    }

    /**
     * 默认HMAC加密
     *
     * @param key     私钥
     * @param content 加密内容
     * @return
     */
    public static String encryptHMAC(String key, String content) {
        try {
            return encryptHMAC(DEFAULT_HMAC, key, content);
        } catch (Exception ex) {
            return content;
        }
    }

    /**
     * 按照红黑树（Red-Black tree）的 NavigableMap 实现
     * 按照字母大小排序
     */
    public static Map<String, String> sort(Map<String, String> map) {
        if (map == null) return null;
        Map<String, String> result = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        result.putAll(map);
        return result;
    }


    /**
     * 组合参数
     *
     * @param map
     * @return 如：key1Value1Key2Value2....
     */
    public static String groupStringParam(Map<String, String> map) {
        if (map == null) return null;
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> item : map.entrySet()) {

            if (TextUtils.isEmpty(item.getValue())) {
                continue;
            }

            sb.append(item.getKey());
            sb.append(item.getValue());
        }
        return sb.toString();
    }

    /**
     * 转成URL 参数
     *
     * @param map
     * @return
     */
    public static String toStringParams(Map<String, String> map) {
        return toStringParams(map, false);
    }

    /**
     * 转成URL 参数
     *
     * @param map
     * @return 如：key1=value1&key2=value2
     */
    public static String toStringParams(Map<String, String> map, boolean enableUrlEncode) {
        Uri.Builder builder = new Uri.Builder();
        for (Map.Entry<String, String> item : map.entrySet()) {

//            if (TextUtils.isEmpty(item.getKey()) || TextUtils.isEmpty(item.getValue()))
//                continue;

            String value = item.getValue();
            if (enableUrlEncode) {
                value = URLEncoder.encode(value);
            }
            builder.appendQueryParameter(item.getKey(), value);
        }
        return builder.build().getQuery();
    }

    /**
     * 3DES加密（使用默认密钥和默认向量）
     *
     * @param plainText 普通文本
     * @return
     * @throws Exception
     */
    public static String encryptDES3(String plainText) throws Exception {
        return encryptDES3(plainText, secretKey, iv);
    }

    /**
     * 3DES加密（指定密钥和指定向量）
     * @param plainText
     * @param sKey
     * @param iv
     * @return
     * @throws Exception
     */
    public static String encryptDES3(String plainText, String sKey, String iv) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(sKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
//        return android.util.Base64.encodeToString(encryptData, android.util.Base64.NO_WRAP);
        return Base64Util.encode(encryptData);
    }

    /**
     * 3DES解密（使用默认密钥和默认向量）
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decodeDES3(String encryptText) throws Exception {
        return decodeDES3(encryptText, secretKey, iv);
    }
    /**
     * 3DES解密（指定密钥和指定向量）
     *
     * @param encryptText 加密文本
     * @param sKey
     * @param iv
     * @return
     * @throws Exception
     */
    public static String decodeDES3(String encryptText, String sKey, String iv) throws Exception {
        DESedeKeySpec spec = new DESedeKeySpec(sKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        Key deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

        byte[] decryptData = cipher.doFinal(Base64Util.decode(encryptText));

        return new String(decryptData, encoding);
    }

    /**
     * DES3 HTTP请求参数生成签名
     * @param params
     * @param md5Pwd
     * @return
     */
    public static String getDES3Sign(Map<String, String> params, String md5Pwd) {
        try {
            if(params==null) params=new HashMap<>();
            StringBuilder buff = new StringBuilder();
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            Set<String> tempSet = new TreeSet();
            for (Map.Entry<String, String> entry : entrySet) {
                tempSet.add(entry.getKey() + entry.getValue());
            }
            for (String param : tempSet) {
                buff.append(param).append("|");
            }
            if(md5Pwd!=null&&md5Pwd.length()==32){
                buff.append(md5Pwd.substring(8, 24));
            }
            return MD5(decodeDES3(buff.toString()));
        } catch (Exception e) {
            Logcat.INSTANCE.e(e.getMessage());
            return "";
        }
    }

    /**
     * md5签名
     * @param string
     * @return
     */
    public static String MD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

}
