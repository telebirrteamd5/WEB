package com.et.pwsdemo.entity.response;

import lombok.Data;

@Data
public class CreateOrderResponse {
    String result;
    String code;
    String msg;
    String nonce_str;
    String sign;
    String sign_type;
    BizContent biz_content;

    @Data
    public class BizContent {
        String merch_order_id;
        String prepay_id;
    }
}
