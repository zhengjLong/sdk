package com.library.base.runit;

import android.content.Context;
import android.util.Log;

import com.library.base.base.BaseApi;
import com.library.base.utils.Logcat;


/**
 * 接口测试组
 */
public final class ApiTestGroupFactory {

    public static ApiTestGroupFactory newInstance(Context context) {
        return new ApiTestGroupFactory(context);
    }

    private BaseApi mApi;

    private ApiTestGroupFactory(Context context) {
        mApi = BaseApi.getInstance(context);
    }

    private void showLog(String msg) {
        Logcat.INSTANCE.e(msg);
    }

    public RUnitTestCaseRunnable<String> getLoginTest(final String phone, final String password) {
        return new XPApiRunnable<String>() {
            @Override
            public void run() {
                mApi.uploadErrorData(phone, this);
            }

            @Override
            public void onApiSuccess(String data) {
                showLog("登录成功：" + data);
            }
        };
    }

//
//    /**
//     * 获取验证码
//     *
//     * @param params 方法名 +请求参数个数
//     * @return
//     */
//    public RUnitTestCaseRunnable<String> getVerifyCode(final String... params) {
//        return new XPApiRunnable<String>() {
//            @Override
//            public void onApiSuccess(String data) {
//                showLog("获得验证码成功：\n" + data);
//            }
//
//            @Override
//            public void run() {
//                //参数为方法名 +请求参数个数
//                Assert.assertEquals(4, params.length);
//            }
//        };
//    }
//
//    /**
//     * 注册
//     *
//     * @param params 方法名 +请求参数个数
//     * @return
//     */
//    public RUnitTestCaseRunnable<UserRegister> getRegister(final String... params) {
//        return new XPApiRunnable<UserRegister>() {
//            @Override
//            public void onApiSuccess(UserRegister data) {
//                showLog("注册成功：\n" + new Gson().toJson(data));
//            }
//
//            @Override
//            public void run() {
//                Assert.assertEquals(5, params.length);
//                mApi.userRegister(params[1], ApiUtil.MD5(params[2]), params[3], params[4], this);
//            }
//        };
//    }
//

}
