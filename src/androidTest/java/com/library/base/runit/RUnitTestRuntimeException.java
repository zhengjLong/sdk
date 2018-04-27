package com.library.base.runit;

/**
 * 测试运行时异常
 */
public class RUnitTestRuntimeException extends RuntimeException {

    public RUnitTestRuntimeException() {
    }

    public RUnitTestRuntimeException(Throwable cause) {
        super(cause);
    }

    public RUnitTestRuntimeException(String message) {
        super(message);
    }

    public RUnitTestRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
}
