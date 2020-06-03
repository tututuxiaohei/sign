package com.mzx.sign;

import javax.servlet.http.HttpServletRequest;

public interface SkipTester {

    /**
     * @return true
     * 返回true 则不校验
     */
    boolean skip(HttpServletRequest httpServletRequest);
}
