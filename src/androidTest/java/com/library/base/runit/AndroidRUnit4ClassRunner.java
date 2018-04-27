package com.library.base.runit;

import android.support.test.InstrumentationRegistry;
import android.util.Log;


import com.library.base.runit.annotation.ASyncMethod;
import com.library.base.utils.Logcat;

import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ChenRui
 */
public class AndroidRUnit4ClassRunner extends BlockJUnit4ClassRunner {

    private static WeakHashMap<String, CountDownLatch> countDownLatchWeakHashMap = new WeakHashMap<>();

    public AndroidRUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Throwable> errors) {
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        ASyncMethod annotation = method.getAnnotation(ASyncMethod.class);
        if (annotation != null)
            return super.methodInvoker(method, test);
        else
            return new SyncInvokeMethod(getTestClass(), method, test);
    }


    public static void finish(String methodName) {
        CountDownLatch countDownLatch = countDownLatchWeakHashMap.get(methodName);
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }


    private class SyncInvokeMethod extends InvokeMethod {
        private CountDownLatch mCountDownLatch = new CountDownLatch(1);

        private String mTestMethodName; // 测试方法名称

        public SyncInvokeMethod(TestClass testClass, FrameworkMethod testMethod, Object target) {
            super(testMethod, target);
            mTestMethodName = testMethod.getName();
            Logcat.INSTANCE.e("测试方法名称：" + mTestMethodName);

        }

        public void evaluateSupport() throws Throwable {
            super.evaluate();
        }

        @Override
        public void evaluate() throws Throwable {
            InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    try {
                        evaluateSupport();
                        countDownLatchWeakHashMap.put(mTestMethodName, mCountDownLatch);
                        mCountDownLatch.await(3, TimeUnit.MINUTES); // 拦截
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }
            });
        }
    }
}
