package com.library.base.runit;



import org.junit.runners.model.InitializationError;

import java.util.List;

/**
 * Created by ChenRui
 */
public class RUnitTestError extends InitializationError {
    public RUnitTestError(String string) {
        super(string);
    }

    public RUnitTestError(List<Throwable> errors) {
        super(errors);
    }

    public RUnitTestError(Throwable error) {
        super(error);
    }

    @Override
    public String getMessage() {
        if (getCauses().isEmpty()) return super.getMessage();
        StringBuilder sb = new StringBuilder("\n");
        for (Throwable throwable : getCauses()) {
            sb.append(throwable);
            sb.append("\n");
        }
        return sb.toString();
    }
}
