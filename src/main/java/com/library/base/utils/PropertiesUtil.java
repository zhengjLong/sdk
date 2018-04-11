package com.library.base.utils;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertiesUtil {


	private static final String PROPERTY_FILE = "c://nine_rect.properties";

    /**
     * 从raw资源文件夹中获取.properties文件。读取key=value序列
     * @param mContext
     * @param key
     * @param resId
     * @return
     */
	public static String readData(Context mContext, String key, int resId) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(mContext.getResources().openRawResource(resId));
			props.load(in);
			in.close();
			String value = props.getProperty(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static void writeData(String key, String value) {
		Properties prop = new Properties();
		try {
			File file = new File(PROPERTY_FILE);
			if (!file.exists())
				file.createNewFile();
			InputStream fis = new FileInputStream(file);
			prop.load(fis);
			fis.close();
			OutputStream fos = new FileOutputStream(PROPERTY_FILE);
			prop.setProperty(key, value);
			prop.store(fos, "Update '" + key + "' value");
			fos.close();
		} catch (IOException e) {
			Logcat.INSTANCE.e("Visit " + PROPERTY_FILE + " for updating "
					+ value + " value error");
		}
	}
}

