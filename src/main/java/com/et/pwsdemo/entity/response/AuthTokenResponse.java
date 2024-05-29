package com.et.pwsdemo.entity.response;

import lombok.Data;

@Data
public class AuthTokenResponse {
    String result;
    String code;
    String msg;
    String nonce_str;
    String sign;
    String sign_type;
    BizContent biz_content;

    @Data
    public class BizContent {
        String open_id;
        String identityId;
        String identityType;
        String walletIdentityId;
    }
}
