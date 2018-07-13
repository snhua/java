package wexin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 微信对账
 */
@Slf4j
public class WxCheckOrderUtil {


    /**
     * 验证支付结果 out_trade_no 和  transaciton_id 二选一查询
     *
     * @param out_trade_no   商户订单号
     * @param transaction_id 微信支付订单号
     * @return true 支付成功
     */
    public static boolean verify(String transaction_id, String out_trade_no, String appId, String mchId, String appKey, String sub_appid, String sub_mch_id) throws Exception {
        SortedMap<String, Object> params = new TreeMap<>();
        //微信开放平台审核通过的应用APPID
        params.put("appid", appId);
        //微信支付分配的商户号
        params.put("mch_id", mchId);
        //随机字符串，不长于32位。推荐随机数生成算法
        params.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
        //商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
        params.put("out_trade_no", out_trade_no);

        if (!StringUtils.isEmpty(sub_appid)) {
            params.put("sub_appid", sub_appid);
            params.put("sub_mch_id", sub_mch_id);
        }

//        params.put("transaction_id", transaction_id);

        String sign = WxPayUtil.createSign(params, appKey, "UTF-8");
        //	请参见签名。
        //检测签名结果
        if (!StringUtils.isEmpty(sign)) {
            log.debug("签名字符串：{}", sign);
            params.put("sign", sign);

            XmlMapper xmlMapper = new XmlMapper();
            ObjectWriter objectWriter = xmlMapper.writer().withRootName("xml");
            String postData = objectWriter.writeValueAsString(params);
            log.debug("提交参数：{}", postData);

            String result = Request.Post(WxApi.WX_VERIFY_PAY_RESULT_URL)
                    .bodyByteArray(postData.getBytes(StandardCharsets.UTF_8), ContentType.APPLICATION_XML)
                    .execute()
                    .returnContent()
                    .asString(StandardCharsets.UTF_8);
            log.debug("请求结果：{}", result);
            //处理返回结果
            String temp = result.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", "");

            Map<String, Object> data = xmlMapper.readValue(temp, new TypeReference<Map<String, String>>() {
            });
            if (data.containsKey("trade_state") && "SUCCESS".equals(data.get("trade_state"))) {
                params.put("trade_state", data.get("trade_state"));
                // params.put("total_fee", data.getString("total_fee"));
                params.put("total_fee", data.get("total_fee"));
                // params.put("time_end", data.getString("time_end"));
                params.put("time_end", data.get("time_end"));
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 付款对账
     *
     * @param out_trade_no
     * @param appId
     * @param mchId
     * @param appKey
     * @return 付款状态
     */
    public static String checkOrderPaySuccess(String out_trade_no, String appId, String mchId, String appKey) throws IOException {
        SortedMap<String, Object> params = new TreeMap<>();
        //微信开放平台审核通过的应用APPID
        params.put("appid", appId);
        //微信支付分配的商户号
        params.put("mch_id", mchId);
        //随机字符串，不长于32位。推荐随机数生成算法
        params.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
        //商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
        params.put("partner_trade_no", out_trade_no);

        String sign = WxPayUtil.createSign(params, appKey, "UTF-8");
        //	请参见签名。
        //检测签名结果
        if (!StringUtils.isEmpty(sign)) {
            params.put("sign", sign);

            XmlMapper xmlMapper = new XmlMapper();
            ObjectWriter objectWriter = xmlMapper.writer().withRootName("xml");

            String postData = objectWriter.writeValueAsString(params);
            log.debug("提交参数：{}", postData);

            String result = Request.Post(WxApi.WX_PAY_RESULT_URL)
                    .bodyByteArray(postData.getBytes(StandardCharsets.UTF_8), ContentType.APPLICATION_XML)
                    .execute()
                    .returnContent()
                    .asString(StandardCharsets.UTF_8);
            log.debug("请求结果：{}", result);

            //处理返回结果
            String temp = result.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", "");
            Map<String, String> data = xmlMapper.readValue(temp, new TypeReference<Map<String, String>>() {
            });
            if (data.containsKey("status")) {
                return data.get("status");
            } else {
                return (data.get("err_code_des"));
            }
        }
        return null;
    }


}
