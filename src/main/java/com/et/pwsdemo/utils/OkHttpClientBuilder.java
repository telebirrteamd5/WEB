package com.et.pwsdemo.utils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class OkHttpClientBuilder {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private static OkHttpClient okHttpClient;

    private static X509TrustManager trustManager;

    public static OkHttpClient createClient() {
        if (okHttpClient == null) {
            try {
                trustManager = getTrustManager();
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
                okHttpClient = new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                        .hostnameVerifier((hostname, session) -> true)
                        .retryOnConnectionFailure(Boolean.TRUE)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return okHttpClient;
    }

    private static X509TrustManager getTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };
    }
}

