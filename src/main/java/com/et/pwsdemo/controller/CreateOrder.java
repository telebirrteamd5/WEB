package com.et.pwsdemo.controller;

import com.et.pwsdemo.config.PWSConfig;
import com.et.pwsdemo.entity.request.CreateOrderRequest;
import com.et.pwsdemo.entity.response.CreateOrderResponse;
import com.et.pwsdemo.service.ApplyFabricTokenService;
import com.et.pwsdemo.utils.OkHttpClientBuilder;
import com.et.pwsdemo.utils.ToolUtils;
import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import static com.et.pwsdemo.utils.OkHttpClientBuilder.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Controller
@RestController
public class CreateOrder {
    @Autowired
    ApplyFabricTokenService applyFabricTokenService;

    /**
     * create a PWS order
     */
    

    @ResponseBody
    @RequestMapping("/create/order")
    public String applyH5Token(@org.springframework.web.bind.annotation.RequestBody CreateOrderRequest input) {
        String fabricToken = applyFabricTokenService.applyFabricToken();
        // input.setAmount("200");
        // input.setTitle("Gift card");
        Map<String, Object> params = createRequestObject(input);
        Gson gson=new Gson();
        String json = gson.toJson(params);
        RequestBody body = FormBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(PWSConfig.BaseUrl + "/payment/v1/merchant/preOrder")
                .addHeader("X-APP-Key", PWSConfig.FabricAppId)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", fabricToken)
                .post(body)
                .build();
        try {
            OkHttpClient client = OkHttpClientBuilder.createClient();
            System.out.println(client);
            Response response = client.newCall(request).execute();
            System.out.println(request);
            System.out.println(response);
            CreateOrderResponse createOrderResponse = new Gson().fromJson(response.body().string(), CreateOrderResponse.class);
            
            return createRawRequest(createOrderResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Map<String, Object> createRequestObject(CreateOrderRequest input) {
        Map<String, Object> req = new HashMap<>();
        req.put("timestamp", ToolUtils.createTimeStamp());
        req.put("nonce_str", ToolUtils.createNonceStr());
        req.put("method", "payment.preorder");
        req.put("version", "1.0");
        Map<String, Object> biz = new HashMap<>();
        req.put("biz_content", biz);
        // fill biz object
        biz.put("notify_url", "https://www.baidu.com");
        biz.put("trade_type", "InApp");
        biz.put("appid", PWSConfig.MerchantAppId);
        biz.put("merch_code", PWSConfig.MerchantCode);
        biz.put("merch_order_id", createMerchantOrderId());
        biz.put("title", input.getTitle());
        biz.put("total_amount", input.getAmount());
        biz.put("trans_currency", "ETB");
        biz.put("timeout_express", "120m");
        System.out.println("starting to put content in to biz");
        System.out.println(biz);
        
        // sign type and sign string
        req.put("sign_type", "SHA256WithRSA");
        req.put("sign", ToolUtils.signRequestBody(req));
        System.out.println();
        System.out.println("createRequestObject: ");
        System.out.println(req);
        return req;
    }

    private String createMerchantOrderId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        return sdf.format(now) + now.getTime();
    }

    private String createRawRequest(CreateOrderResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("appid", PWSConfig.MerchantAppId);
        map.put("merch_code", PWSConfig.MerchantCode);
        map.put("nonce_str", ToolUtils.createNonceStr());
        map.put("prepay_id", response);
        map.put("timestamp", ToolUtils.createTimeStamp());
        String sign = ToolUtils.signRequestBody(map);
        String rawRequest = "";
        for (String key : map.keySet()) {
            rawRequest += key + "=" + map.get(key) + "&";
        }
        rawRequest += "sign=" + sign + "&sign_type=SHA256WithRSA";
        return rawRequest;
    }
}