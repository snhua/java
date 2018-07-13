package wexin.pay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.snhua.encryption.md5.Md5SignUtil;
import com.snhua.encryption.rsa.common.RSAUtil;
import com.snhua.format.XmlUtil;
import com.snhua.https.HttpsRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class WxPayToCard {
    private static String url = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";
    private static String url_query_bank = "https://api.mch.weixin.qq.com/mmpaysptrans/query_bank";


    public static Map<String, String> payWxToCard(String mch_id, byte[] p12, String publicKey, String apiKey, String partner_trade_no,
                                                  String nonce_str, String enc_bank_no, String enc_true_name,
                                                  String bank_code, int amount) {
        String xmlData = null;
        try {
            xmlData = getXmlString(publicKey, apiKey, mch_id, partner_trade_no, nonce_str, enc_bank_no, enc_true_name,
                    bank_code, amount);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
        log.debug(xmlData);
        String payResult = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpsRequest.getCloseableHttpClient(p12, mch_id);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
        try {
            payResult = HttpsRequest.sslRequestPost(httpClient, url, xmlData);
        } catch (Exception e) {
            e.printStackTrace();
            HashMap<String, String> map = new HashMap<>();
            map.put("request_tag", xmlData);

            return map;

        }
        log.debug("支付结果:{}", payResult);

        //解析返回的xml
        String temp = payResult.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", "");

        XmlMapper xmlMapper = new XmlMapper();
        Map<String, String> data = null;
        try {
            data = xmlMapper.readValue(temp, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            data = new HashMap<>();
        }
        data.put("return_tag", payResult);
        data.put("request_tag", xmlData);

        return data;


    }

    private static String getXmlString(String publicKey, String apiKey, String mch_id, String partner_trade_no,
                                       String nonce_str, String enc_bank_no, String enc_true_name, String bank_code, int amount) throws Exception {
        Map<String, String> param = new HashMap<>();

        param.put("mch_id", mch_id);
        param.put("partner_trade_no", partner_trade_no);
        param.put("nonce_str", nonce_str);
        param.put("enc_bank_no", RSAUtil.encryptDataAndBase64ToString(enc_bank_no, publicKey));
        param.put("enc_true_name", RSAUtil.encryptDataAndBase64ToString(enc_true_name, publicKey));
        param.put("bank_code", bank_code);
        param.put("amount", String.valueOf(amount));

        param.put("sign", Md5SignUtil.md5Sign(param, "&key=" + apiKey).toUpperCase());
        return XmlUtil.mapToXml(param);
    }

    public static Map<String, String> query(String mch_id, byte[] p12, String apikey, String partner_trade_no) throws Exception {
        String nonce_str = UUID.randomUUID().toString().substring(0, 32);
        String xmlData = getXmlString(mch_id, apikey, partner_trade_no, nonce_str);
        log.debug(xmlData);
        CloseableHttpClient httpClient = HttpsRequest.getCloseableHttpClient(p12, mch_id);
        String payResult = HttpsRequest.sslRequestPost(httpClient, url_query_bank, xmlData);
        log.debug("查询结果:{}", payResult);
        //解析返回的xml
        String temp = payResult.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", "");

        XmlMapper xmlMapper = new XmlMapper();
        Map<String, String> data = xmlMapper.readValue(temp, new TypeReference<Map<String, String>>() {
        });
        return data;


    }

    private static String getXmlString(String mch_id, String apikey, String partner_trade_no, String nonce_str) throws Exception {
        Map<String, String> param = new HashMap<>();

        param.put("mch_id", mch_id);
        param.put("partner_trade_no", partner_trade_no);
        param.put("nonce_str", nonce_str);

        param.put("sign", Md5SignUtil.md5Sign(param, "&key=" + apikey).toUpperCase());
        return XmlUtil.mapToXml(param);
    }
}
