package com.library.base.runit.apiTest;


import com.library.base.net.HttpCallback;
import com.library.base.runit.ApiArgumentListener;
import com.library.base.runit.ArgumentRUnit;
import com.library.base.runit.RUnitArgumentTestListener;
import com.library.base.runit.RUnitTestRuntimeException;
import com.library.base.runit.argument.ArgumentElement;
import com.library.base.runit.argument.ArgumentRule;
import com.library.base.runit.argument.ArgumentTestClass;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

/**
 * demo API 测试
 */
@RunWith(ArgumentRUnit.class)
@ArgumentTestClass(configPath = "UserApiTestRule.json")
public class DemoApiTest extends BaseApiTest {

    String mPhone = "xxxxxxxxxxxx";
    String mPassword = "123456";

    @Override
    @Before
    public void setup() {
        super.setup();
//        必要的初始数据
        testLogin();
    }

    /**
     * 登录接口
     */
    public void testLogin() {
        put(mTestGroupFactory.getLoginTest(mPhone, mPassword));
        put(mTestGroupFactory.getLoginTest(mPhone, mPassword));
        startTest();
    }


    /**
     * 注解的文件中查找对应方法名的传入参数
     */
    @Test
    @ArgumentRule.configMethod
    public void testRegSmsCode(String phone, String type, String memberCode, RUnitArgumentTestListener<String> listener) {
        mApi.uploadErrorData(phone, new ApiArgumentListener<>(listener));
    }


    /**
     * 自定义注解参数
     * @param phone
     * @param pwd
     * @param listener
     */
    @Test
    @ArgumentRule({
            @ArgumentElement(name = "正常登录", value = {"18926247331", "123456"}),
            @ArgumentElement(name = "空值测试", value = {"", ""}, successMessage = "登录有风险！！"),
            @ArgumentElement(name = "错误的手机号码测试", value = {"15918176309", "123456"}, successMessage = "登录有风险！！"),
            @ArgumentElement(name = "错误的手机号码和密码测试", value = {"123sdfsd", "adf"}, successMessage = "登录有风险！！"),
            @ArgumentElement(name = "敏感数据测试", value = {"1' or 1=1", "1' or 1=1"}, successMessage = "登录有风险！！"),
            @ArgumentElement(name = "非法号码测试", value = {"——123+234e", "adf111"}, successMessage = "登录有风险！！")
    })
    public void testLogins(String phone, String pwd, RUnitArgumentTestListener<String> listener) {
        mApi.uploadErrorData(phone, new ApiArgumentListener<>(listener));
    }

    @Test
    @ArgumentRule({
            @ArgumentElement(name = "错误的银行名称", value = {"大神银行", "", "", ""}, successMessage = "实名认证有风险"),
            @ArgumentElement(name = "错误的银行信息", value = {"大神银行", "送达方式的12312312", "", ""}, successMessage = "实名认证有风险"),
            @ArgumentElement(name = "错误的身份证信息", value = {"中国银行", "6214835191143906", "sdfsdf12312312312", "xxx"}, successMessage = "实名认证有风险"),
            @ArgumentElement(name = "正确的实名认证信息", value = {"中国银行", "6214835191143906", "120222199801010274", "name"})
    })
    public void testDoUserAuthApi(String bankName, String bankNumber, String idCard, String realName, RUnitArgumentTestListener<String> listener) {

        mPhone = "18121210350";
        blockUse(mPhone);
        mApi.uploadErrorData(realName, new ApiArgumentListener<>(listener));
    }

    /**
     * 阻塞调用
     * @param phone
     */
    public void blockUse(final String phone) {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        mApi.uploadErrorData("", new HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(int errorCode, String message) {
                throw new RUnitTestRuntimeException("第一次调用失败：" + phone + message);
            }
        });

        mApi.uploadErrorData("", new HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(int errorCode, String message) {
                throw new RUnitTestRuntimeException("第二次调用失败：" + phone + ";" + message);
            }
        });

    }



}
