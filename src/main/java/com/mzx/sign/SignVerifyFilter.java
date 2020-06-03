package com.mzx.sign;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TreeMap;

@Slf4j
public class SignVerifyFilter implements Filter {


    private SignProperties signProperties;

    private SkipTester skipTester;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        res.addHeader("currentServerTime", Long.valueOf(System.currentTimeMillis()).toString());
        log.info("doFilter | sign start");
        //跳过校验逻辑
        if (!signProperties.isEnable()
                && skipTester.skip(req)) {
            log.info("doFilter | sign verify skip URI is {}", req.getRequestURI());
            filterChain.doFilter(req, res);
            return;
        }
        //校验时间
        verifyTimestamp(req, res);

        //校验入参 sign
        if (!verifySign(req)) {
            log.info("doFilter | sign verify error URI is {}", req.getRequestURI());
            writeErrorMsgTo(req, res);
            return;
        }
        filterChain.doFilter(req, res);
    }

    private void writeErrorMsgTo(HttpServletRequest req, HttpServletResponse res) {

        res.addHeader("content-type", "application/json");
        SignProperties.Response errorResponse = signProperties.getErrorResponse();
        String msg = signProperties.getErrorMsg();
        res.setStatus(errorResponse.getHttpCode());

        try (ServletOutputStream outputStream = res.getOutputStream()) {
            outputStream.write(String.format(errorResponse.getJsonBodyTemplate(), msg).getBytes());
            outputStream.flush();
        } catch (Exception e) {
        }
    }

    private boolean verifySign(HttpServletRequest req) {
        //拿到所有参数 按首字母顺序排序
        TreeMap<String, String> paramMap = getTreeMapByReq(req);
        return true;
    }

    private TreeMap<String, String> getTreeMapByReq(HttpServletRequest req) {
        //获取非json的参数 (param, header, 表单)
        return null;
    }

    private void verifyTimestamp(HttpServletRequest req, HttpServletResponse res) {
        String timestampReq = req.getHeader("timestamp");
        //校验时间有效性
        Long timestamp = null;
        try {
            timestamp = Long.valueOf(timestampReq);
        } catch (NumberFormatException e) {
            writeErrorMsgTo(req, res);
            log.info("doFilter | sign verify timestamp error is {}", timestampReq);
            return;
        }
        if ((System.currentTimeMillis() - timestamp) > signProperties.getSignTimeOutInMillisecond()) {
            writeErrorMsgTo(req, res);
            log.info("doFilter | sign verify timestamp overtime is {}", timestamp);
            return;
        }
    }

    @Override
    public void destroy() {

    }
}
