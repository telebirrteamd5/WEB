package com.et.pwsdemo.entity.request;

import lombok.Data;

@Data
public class AuthTokenRequest {
    /**
     * token from app. or test apply from interface "applyH5Token"
     */
    private String authToken;
}
