package com.library.base.runit;

import android.util.Log;

import com.library.base.utils.Logcat;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Test case Log helper
 */
public class RUnitTestLogUtils {

    /**
     * 打印对象
     *
     * @param obj object
     */
    public static void printfObject( Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Logcat.INSTANCE.e( field.getName() + " = " + field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打印List对象
     */
    public static <T> void printfList( List<T> data) {
        for (T t : data) {
            printfObject( t);
            Logcat.INSTANCE.e( "---------------------------");
        }
    }


    /**
     * 输出泛型对象
     *
     * @param data
     */
    public static <T> void print( T data) {
        if (data == null) {
            Logcat.INSTANCE.e( "打印对象为空");
            return;
        }
        Logcat.INSTANCE.e("=====================================================");
        Logcat.INSTANCE.e( "打印对象信息：");
        Logcat.INSTANCE.e( "--------------");
        if (data instanceof Void) {
            Logcat.INSTANCE.e( "无打印信息，对象为Void 类型。");
        }
        if (data instanceof List) {
            printfList((List) data);
        } else if (data.getClass().isArray()) {
            printfList(Arrays.asList(data));
        } else {
            printfObject(data);
        }
        Logcat.INSTANCE.e( "=====================================================");
    }

}
