package com.library.base.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.HashMap;


/**
 * 联系人
 */
public class ContractsUtil {

    private static final String[] CONTACTOR_ION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    /**
     * 拨打电话,不需要权限
     *
     * @param context
     * @param phoneNumber
     */
    public static void callPhone(Context context, String phoneNumber) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel://" + phoneNumber)));
    }

    /**
     * 发送短信息，跳转到发送短信界面
     *
     * @param context
     * @param phoneNumber
     * @param content
     */
    public static void sendSms(Context context, String phoneNumber, String content) {
        Uri uri = Uri.parse("smsto:" + (TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", TextUtils.isEmpty(content) ? "" : content);
        context.startActivity(intent);
    }

    /**
     * 获取联系人,性能优化
     * <p>
     * 遍历方法
     * Set<Map.Entry> entries = map.entrySet();
     * for (Map.Entry entry : entries) {
     * Object key=entry.getKey();//phone
     * Object value=entry.getValue();//name
     * }
     *
     * @param context
     * @return 电话号码
     */
    public static HashMap getContacts(Context context) {
        HashMap map = new HashMap();
        Cursor phones = null;
        ContentResolver cr = context.getContentResolver();
        try {
            phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, CONTACTOR_ION, null, null, "sort_key");
            if (phones != null) {
                final int displayNameIndex = phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneString, displayNameString;
                while (phones.moveToNext()) {
                    phoneString = phones.getString(phoneIndex);
                    displayNameString = phones.getString(displayNameIndex);
                    map.put(phoneString, displayNameString);
                }
            }
        } catch (Exception e) {
            Logcat.INSTANCE.e(e.getMessage());
        } finally {
            if (phones != null)
                phones.close();
        }
        return map;
    }
}
