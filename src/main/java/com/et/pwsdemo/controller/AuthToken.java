package com.et.pwsdemo.controller;

import com.et.pwsdemo.config.PWSConfig;
import com.et.pwsdemo.entity.request.AuthTokenRequest;
import com.et.pwsdemo.entity.response.AuthTokenResponse;
import com.et.pwsdemo.service.ApplyFabricTokenService;
import com.et.pwsdemo.utils.OkHttpClientBuilder;
import com.et.pwsdemo.utils.ToolUtils;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.et.pwsdemo.utils.OkHttpClientBuilder.JSON;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Controller
public class AuthToken {
    @Autowired
    ApplyFabricTokenService applyFabricTokenService;
    /**
     * verify the token from app. or from the interface 'ApplyH5Token'
     */
    @ResponseBody
    @RequestMapping("/auth/token")
    public AuthTokenResponse authToken(@org.springframework.web.bind.annotation.RequestBody AuthTokenRequest input) {
        String fabricToken = applyFabricTokenService.applyFabricToken();
        Map<String, Object> params = createRequestObject(input);
        RequestBody body = RequestBody.create(new Gson().toJson(params), JSON);
        Request request = new Request.Builder()
                .url(PWSConfig.BaseUrl + "/payment/v1/auth/authToken")
                .addHeader("X-APP-Key", PWSConfig.FabricAppId)
                .addHeader("Authorization", fabricToken)
                .post(body)
                .build();
        try {
            OkHttpClient client = OkHttpClientBuilder.createClient();
            Response response = client.newCall(request).execute();
            return new Gson().fromJson(response.body().string(), AuthTokenResponse.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Map<String, Object> createRequestObject(AuthTokenRequest input) {
        Map<String, Object> req = new HashMap<>();
        req.put("timestamp", ToolUtils.createTimeStamp());
        req.put("nonce_str", ToolUtils.createNonceStr());
        req.put("method", "payment.authtoken");
        req.put("version", "1.0");
        Map<String, Object> biz = new HashMap<>();
        req.put("biz_content", biz);

        // fill biz object
        biz.put("access_token", input.getAuthToken());
        biz.put("trade_type", "InApp");
        biz.put("appid", PWSConfig.MerchantAppId);
        biz.put("resource_type", "OpenId");

        // sign type and sign string
        req.put("sign_type", "SHA256WithRSA");
        req.put("sign", ToolUtils.signRequestBody(req));
        return req;
    }
}
