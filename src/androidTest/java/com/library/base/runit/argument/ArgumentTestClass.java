package com.library.base.runit.argument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 参数化类配置
 */
@Target(ElementType.TYPE)
public @interface ArgumentTestClass {
    /**
     * 所有的配置文件地址，如果有配置，测试方法就可以不用配置文件地址了。
     *
     * @return assert 文件地址
     */
    String configPath() default "";
}
