package wexin.pay;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import wexin.WxApi;
import wexin.WxPayUtil;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by SongpoLiu on 2017/6/11.
 */
@Slf4j
public class WeChatEnterprisePayService {

    /**
     * 创建订单-普通商户
     *
     * @param appId          小程序标识
     * @param mchId          商户号
     * @param partnerTradeNo 商户订单号
     * @param openId         用户openid
     * @param checkName      是否实名认证
     * @param reUserName     收款方姓名
     * @param amount         金额
     * @param desc           描述信息
     * @param spbillCreateIp 终端IP
     * @param apiKey         商户支付密钥
     * @param cert           支付证书
     * @return 业务消息
     */
    public Map<String, Object> pay(String appId, String mchId, String partnerTradeNo, String openId, Boolean checkName, String reUserName,
                                   Integer amount, String desc, String spbillCreateIp, String apiKey, byte[] cert) {
        Map<String, Object> data = new HashMap<>();
        try {
            TreeMap<String, Object> params = new TreeMap<>();
            // 公众账号appid
            // mch_appid
            // 是
            // wx8888888888888888
            // String
            // 微信分配的公众账号ID（企业号corpid即为此appId）
            params.put("mch_appid", appId);
            // 商户号
            // mchid
            // 是
            // 1900000109
            // String(32)
            // 微信支付分配的商户号
            params.put("mchid", mchId);
            // 设备号
            // device_info
            // 否
            // String(32)
            // 013467007045764
            // 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
            params.put("device_info", "WEB");
            // 随机字符串
            // nonce_str
            // 是
            // String(32)
            // 5K8264ILTKCH16CQ2502SI8ZNMTM67VS
            // 随机字符串，不长于32位。推荐随机数生成算法
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_3
            params.put("nonce_str", WxPayUtil.getRandomUpperStringByLength(32));
            // 商户订单号
            // partner_trade_no
            // 是
            // 10000098201411111234567890
            // String
            // 商户订单号，需保持唯一性 (只能是字母或者数字，不能包含有符号)
            params.put("partner_trade_no", partnerTradeNo);
            // 用户openid
            // openid
            // 是
            // oxTWIuGaIt6gTKsQRLau2M0yL16E	String
            // 商户appid下，某用户的openid
            params.put("openid", openId);
            // 校验用户姓名选项
            // check_name
            // 是
            // FORCE_CHECK
            // String
            // NO_CHECK：不校验真实姓名 FORCE_CHECK：强校验真实姓名
            params.put("check_name", checkName ? "FORCE_CHECK" : "NO_CHECK");
            // 收款用户姓名
            // re_user_name
            // 可选
            // 马花花
            // String
            // 收款用户真实姓名。如果check_name设置为FORCE_CHECK，则必填用户真实姓名
            if (checkName) {
                params.put("re_user_name", reUserName);
            }
            // 金额
            // amount
            // 是
            // 10099
            // int
            // 企业付款金额，单位为分
            params.put("amount", amount);
            // 企业付款描述信息
            // desc
            // 是
            // 理赔
            // String
            // 企业付款操作说明信息。必填。
            params.put("desc", desc);
            // Ip地址
            // spbill_create_ip
            // 是
            // 192.168.0.1
            // String(32)
            // 调用接口的机器Ip地址
            params.put("spbill_create_ip", spbillCreateIp);

            //
            String sign = WxPayUtil.createSign(params, apiKey);

            //检测签名结果
            if (StringUtils.isNotBlank(sign)) {
                log.debug("签名字符串：{}", sign);
                params.put("sign", sign);

                XmlMapper xmlMapper = new XmlMapper();

                ObjectWriter writer = xmlMapper.writer().withRootName("xml");

                byte[] postData = writer.writeValueAsBytes(params);

                log.debug("提交参数：{}", new String(postData, StandardCharsets.UTF_8));

                //指定读取证书格式为PKCS12
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                //读取本机存放的PKCS12证书文件
                try (ByteArrayInputStream instream = new ByteArrayInputStream(cert)) {
                    //指定PKCS12的密码(商户ID)
                    keyStore.load(instream, mchId.toCharArray());

                    SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
                    //指定TLS版本
                    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
                    //设置httpclient的SSLSocketFactory
                    HttpClient httpclient = HttpClients.custom()
                            .setSSLSocketFactory(sslsf)
                            .build();

                    Executor executor = Executor.newInstance(httpclient);

                    String result = executor.execute(Request.Post(WxApi.WX_ENTERPRISE_PAY_URL)
                            .bodyByteArray(postData, ContentType.APPLICATION_XML))
                            .returnContent()
                            .asString(StandardCharsets.UTF_8);
                    log.debug("响应结果：{}", result);
                    //处理返回结果
                    String temp = result.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", "");

                    log.debug("响应结果: {}", temp);

                    data = xmlMapper.readValue(temp, new TypeReference<Map<String, String>>() {
                    });
                    if (data.containsKey("payment_no")) {

                        // 设置ApiKey
                        params.put("apikey", apiKey);

                        // 设置请求包
                        data.put("request_tag", xmlMapper.writeValueAsString(params));

                        // 设置响应包
                        data.put("return_tag", temp);
                    } else {
                        log.error("创建订单失败，{}", temp);
                    }
                } catch (Exception e) {
                    log.error("读取证书失败，{}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("签名失败，{}", e.getMessage());
        }
        return data;
    }
}
