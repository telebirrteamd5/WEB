package com.et.pwsdemo.entity.response;

import lombok.Data;

@Data
public class ApplyH5TokenResponse {
    String result;
    String code;
    String msg;
    String nonce_str;
    String sign;
    String sign_type;
    BizContent biz_content;

    @Data
    public class BizContent {
        String access_token;
        String appid;
        String merch_entry_url;
        String registered_time;
        String status;
    }
}
