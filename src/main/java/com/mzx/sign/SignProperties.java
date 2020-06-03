package com.mzx.sign;

import lombok.Data;

@Data
public class SignProperties {

    private boolean enable = true;

    // 5 min 有效时间 ，单位毫秒
    private Long signTimeOutInMillisecond = 1000L * 60 * 5;

    private Response errorResponse = new Response();

    private String errorMsg = "验签失败，请联系核查相关参数是否匹配！";

    @Data
    public static class Response {
        private int httpCode = 400;
        private String jsonBodyTemplate = "{\"status\":400403,\"msg\":\"%s\",\"data\":null}";
    }
}
