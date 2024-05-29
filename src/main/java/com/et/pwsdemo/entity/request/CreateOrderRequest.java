package com.et.pwsdemo.entity.request;

import lombok.Data;

@Data
public class CreateOrderRequest {
    /**
     * goods name
     */
    String title;
    /**
     * goods price, just number
     */
    String amount;
}
