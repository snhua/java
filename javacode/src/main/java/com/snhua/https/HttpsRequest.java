package com.snhua.https;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;


/**
 * #1
 * HTTPS 双向认证 - direct into cacerts
 *
 * @Author Ye_Wenda
 * @Date 7/11/2017
 */
@Slf4j
public class HttpsRequest {


    public static CloseableHttpClient getCloseableHttpClient(byte[] p12_array, String pwd) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream instream = new ByteArrayInputStream(p12_array);
        try {
            // 这里就指的是KeyStore库的密码
            keyStore.load(instream, pwd.toCharArray());
        } finally {
            instream.close();
        }

        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, pwd.toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext
                , new String[]{"TLSv1"}  // supportedProtocols ,这里可以按需要设置
                , null  // supportedCipherSuites
                , SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        return httpclient;
    }

    public static String sslRequestPost(CloseableHttpClient httpclient, String url, String param) throws IOException {

        try {
            HttpPost httpPost = new HttpPost(url);
            // 设置参数

            StringEntity s = new StringEntity(param);
            s.setContentEncoding("UTF-8");
            s.setContentType("text/xml");
            httpPost.setEntity(s);
            HttpResponse response = httpclient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    String result = EntityUtils.toString(resEntity, "utf-8");
//                    log.debug(result);
                    return result;
                }
            }
        } finally {
            httpclient.close();
        }
        return null;
    }


}