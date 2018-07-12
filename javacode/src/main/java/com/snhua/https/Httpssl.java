package com.snhua.https;

import com.snhua.http.HttpClientUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author kobe
 */
public class Httpssl {

    public static final String username = "";
    public static final String password = "";
    public static final String ip = "";
    public static final int port = 443;
    /**
     * 重写验证方法，取消检测ssl
     */
    public TrustManager truseAllManager = new X509TrustManager() {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException {
        }

    };

    /**
     * Creates {@link CloseableHttpClient} instance with default
     * configuration.
     */
    public static CloseableHttpClient createDefault() {
        return HttpClientBuilder.create().build();
    }

    /**
     * @param requestUrl
     * @param xmlData
     * @param contentType
     * @param charset
     */
    public String postRequest(String requestUrl, String xmlData, String contentType, String charset) {

        int returncode = 0;
        String msg = "";
        // 1. 创建HttpClient对象。
        HttpClient httpClient = createDefault();
        //  2.创建请求方法的实例，并指定请求URL。如果需要发送GET请求，创建HttpGet对象；如果需要发送POST请求，创建HttpPost对象。
        HttpPost post = new HttpPost(requestUrl);
        try {
            // 3. 如果需要发送请求参数，
            StringEntity entity = new StringEntity(xmlData, charset);
            entity.setContentType(contentType);
            post.setEntity(entity);
            //3.1访问https的网站设置ssl
            enableSSL(httpClient);
            //3.2设置超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30 * 1000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);
            //3.3设置basic基本认证
            BasicHttpContext basicHttpContext = enableBasic(httpClient, username, password, ip, port);
            //4. 调用HttpClient对象的execute
            HttpResponse response = httpClient.execute(post, basicHttpContext);
            // 5. 调用HttpResponse的getAllHeaders()、getHeaders(String
            // name)等方法可获取服务器的响应头；调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。
            returncode = response.getStatusLine().getStatusCode();
            System.out.println("postCode= " + returncode);
            // 若状态值为2类，则ok
            if (200 <= returncode && returncode < 300) {
//                System.out.println("数据发送成功！");
                return HttpClientUtil.getHttpEntityContent(response);
            } else {
                HttpEntity entityRep = response.getEntity();
                if (entityRep != null) {
                    msg = EntityUtils.toString(response.getEntity(), "UTF-8");
                    System.out.println("错误信息" + msg);
                    return "错误信息" + msg;
                }
                return "";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            // 关闭连接释放资源
            if (null != post) {
                post.releaseConnection();

            }
            if (null != httpClient) {
                httpClient.getConnectionManager().shutdown();
            }

        }

    }

    public BasicHttpContext enableBasic(HttpClient httpClient, String username, String password, String ip, int port) {
        AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
//        httpClient.getCredentialsProvider().setCredentials(authScope, credentials);
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        HttpHost targetHost = new HttpHost(ip, port, "https");
        authCache.put(targetHost, basicAuth);
        // Add AuthCache to the execution context
        BasicHttpContext localcontext = new BasicHttpContext();
        localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

        return localcontext;
    }

    public InputStream postInputStream(String requestUrl, String xmlData, String contentType, String charset, FileOutputStream fout) {
        int returncode = 0;
        String msg = "";
        // 1. 创建HttpClient对象。
        DefaultHttpClient httpClient = new DefaultHttpClient();
        //  2.创建请求方法的实例，并指定请求URL。如果需要发送GET请求，创建HttpGet对象；如果需要发送POST请求，创建HttpPost对象。
        HttpPost post = new HttpPost(requestUrl);
        try {
            // 3. 如果需要发送请求参数，
            StringEntity entity = new StringEntity(xmlData, charset);
            entity.setContentType(contentType);
            post.setEntity(entity);
            //3.1访问https的网站设置ssl
            enableSSL(httpClient);
            //3.2设置超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30 * 1000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);
            //3.3设置basic基本认证
            BasicHttpContext basicHttpContext = enableBasic(httpClient, username, password, ip, port);
            //4. 调用HttpClient对象的execute
            HttpResponse response = httpClient.execute(post, basicHttpContext);
            // 5. 调用HttpResponse的getAllHeaders()、getHeaders(String
            // name)等方法可获取服务器的响应头；调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。
            returncode = response.getStatusLine().getStatusCode();
            System.out.println("postCode= " + returncode);
            // 若状态值为2类，则ok
            HttpEntity entityRep = response.getEntity();
            if (entityRep != null) {
                IOUtils.copy(entityRep.getContent(), fout);
            }
            return null;

        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        } finally {
            // 关闭连接释放资源
            if (null != post) {
                post.releaseConnection();

            }
            if (null != httpClient) {
                httpClient.getConnectionManager().shutdown();
            }

        }

    }

    /**
     * 访问https的网站
     *
     * @param httpclient
     */
    public void enableSSL(HttpClient httpclient) {
        // 调用ssl
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{truseAllManager}, null);
            SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme https = new Scheme("https", sf, 443);
            httpclient.getConnectionManager().getSchemeRegistry().register(https);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String https() throws IOException {
        URL reqURL = new URL("https://www.cnblogs.com/chinway/p/5802541.html"); //创建URL对象
        HttpsURLConnection httpsConn = (HttpsURLConnection) reqURL.openConnection();

/*下面这段代码实现向Web页面发送数据，实现与网页的交互访问
httpsConn.setDoOutput(true);
OutputStreamWriter out = new OutputStreamWriter(huc.getOutputStream(), "8859_1");
out.write( "……" );
out.flush();
out.close();
*/

//取得该连接的输入流，以读取响应内容
        InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream());
        return IOUtils.toString(insr);


    }

    public static void main(String[] ar){
        try {
          System.out.println(  https());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
