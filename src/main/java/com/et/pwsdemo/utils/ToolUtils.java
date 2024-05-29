package com.et.pwsdemo.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.et.pwsdemo.config.PWSConfig;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

public class ToolUtils {
    // exclude field to sign.
    private static List<String> excludes = Arrays.asList("sign", "sign_type", "header", "refund_info", "openType", "raw_request");

    public static String createTimeStamp() {
        return new Date().getTime() + "";
    }

    public static String createNonceStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String signRequestBody(Map<String, Object> req) {
        Map<String, String> map = new HashMap<>();
        for (String key : req.keySet()) {
            if (excludes.contains(key)) {
                continue;
            }
            Object val = req.get(key);
            if (val instanceof String) {
                map.put(key, val.toString());
            }
        }
        Map<String, Object> biz = (Map<String, Object>) req.get("biz_content");        
        if (biz != null) {
            for (String key : biz.keySet()) {
                if (excludes.contains(key)) {
                    continue;
                }
                Object val = biz.get(key);
                if (val instanceof String) {
                    map.put(key, val.toString());
                }
            }
        }
        List<String> keys = new ArrayList<>();
        keys.addAll(map.keySet());
        Collections.sort(keys);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String val = map.get(key);
            list.add(key + "=" + val);
        }
        // signString format example: "appid=xxxxx&app_code=xxxxxxxx&access_token=xxxx-xxxxx-xxxx-xxxxx&nonce_str=xxxxxxx&rade_type=InApp"
        String signString = String.join("&", list);
        return signSHA256WithRSA(signString);
    }

    private static String signSHA256WithRSA(String input) {
        String signature = null;
        try {
            PrivateKey privateKey = getPrivateKey(PWSConfig.MerchantPrivateKey);

            //Signature sign = Signature.getInstance("SHA256withRSA");
            Signature sign = Signature.getInstance("SHA256withRSA/PSS", new BouncyCastleProvider());
            sign.initSign(privateKey);
            sign.update(input.getBytes());
            byte[] signed = sign.sign();
            signature = Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    public static PrivateKey getPrivateKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
