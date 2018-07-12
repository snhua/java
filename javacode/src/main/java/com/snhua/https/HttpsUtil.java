package com.snhua.https;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

public class HttpsUtil {
    public static String send(String url, String method, String param) throws IOException {
        URL reqURL = new URL(url); //创建URL对象
        HttpsURLConnection httpsConn = (HttpsURLConnection) reqURL.openConnection();


//        httpsConn.setSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());
//下面这段代码实现向Web页面发送数据，实现与网页的交互访问
        if(StringUtils.isNoneEmpty(param)) {
            httpsConn.setDoOutput(true);
            httpsConn.setRequestMethod(method);

            OutputStreamWriter out = new OutputStreamWriter(httpsConn.getOutputStream(), "utf-8");
            out.write(param);
            out.flush();
            out.close();
        }


//取得该连接的输入流，以读取响应内容
        InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream());
        return IOUtils.toString(insr);


    }

    public static void main(String[] a) {
        String u ="https://www.baidu.com";
        try {
            String p="";
            System.out.println( send(u,"post",p));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
