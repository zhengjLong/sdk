package com.library.base.runit.apiTest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.library.base.base.BaseApi;
import com.library.base.net.HttpEngine;
import com.library.base.runit.ApiTestGroupFactory;
import com.library.base.runit.RUnitTestCaseQueue;
import com.library.base.runit.RUnitTestCaseRunnable;
import com.library.base.utils.SdkPreference;

import org.junit.Before;
import org.junit.runner.RunWith;


/**
 * 接口测试
 */

@RunWith(AndroidJUnit4.class)
public abstract class BaseApiTest {

    protected Context mContext;
    protected BaseApi mApi;
    protected ApiTestGroupFactory mTestGroupFactory;
    private RUnitTestCaseQueue mGroupTestCase;


    @Before
    public void setup() {
        mContext = InstrumentationRegistry.getTargetContext();
        HttpEngine.init(mContext);
        SdkPreference.instance(mContext);
        mApi = BaseApi.getInstance(mContext);
        mGroupTestCase = new RUnitTestCaseQueue(InstrumentationRegistry.getInstrumentation(), false);
        mTestGroupFactory = ApiTestGroupFactory.newInstance(mContext);
    }

    public void put(RUnitTestCaseRunnable runnable) {
        mGroupTestCase.put(runnable);
    }

    public void startTest() {
        mGroupTestCase.start();
    }


    public String getWid() {
        return SdkPreference.getInstance().getToken();
    }

}
