package wexin.rec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import wexin.WxApi;
import wexin.WxPayUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by snhua on 2017/6/3.
 */
@Slf4j
public class WeChatOrder {


    /**
     * 创建订单-普通商户（微信）
     *
     * @param appId          小程序标识
     * @param mchId          商户号
     * @param body           商品描述
     * @param detail         商品详情
     * @param attach         附加数据
     * @param outTradeNo     商户订单号
     * @param totalFee       总金额
     * @param spbillCreateIp 终端IP
     * @param timeStart      订单创建时间
     * @param timeExpire     订单失效时间
     * @param goodsTag       商品标记
     * @param limitPay       指定支付方式
     * @param openId         用户标识
     * @param apiKey         商户支付密钥
     * @param notify_url
     * @return 业务消息
     */
    public Map<String, Object> merchantPay(String appId, String mchId, String body, String detail, String attach,
                                           String outTradeNo, Integer totalFee, String spbillCreateIp, String timeStart, String timeExpire, String goodsTag,
                                           boolean limitPay, String openId, String apiKey, String notify_url) {
        Map<String, Object> data = null;
        try {
            TreeMap<String, Object> params = new TreeMap<>();
            // 小程序ID
            // appid
            // 是
            // String(32)
            // 微信分配的小程序ID
            params.put("appid", appId);
            // 商户号
            // mch_id
            // 是
            // String(32)
            params.put("mch_id", mchId);
            // 设备号
            // device_info
            // 否
            // String(32)
            // 013467007045764
            // 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
            // params.put("device_info", "");
            // 随机字符串
            // nonce_str
            // 是
            // String(32)
            // 随机字符串，不长于32位。推荐随机数生成算法
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_3
            params.put("nonce_str", WxPayUtil.getRandomUpperStringByLength(32));
            // 签名类型
            // sign
            // 是
            // String(32)
            // C380BEC2BFD727A4B6845133519F3AD6
            // 签名，详见签名生成算法
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_3
            params.put("sign_type", "MD5");
            // 商品描述
            // body
            // 是
            // String(128)
            // 腾讯充值中心-QQ会员充值
            // 商品简单描述，该字段须严格按照规范传递，具体请见参数规定
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("body", body);
            // 商品详情
            // detail
            // 否
            // String(6000)
            // 商品详细列表，使用Json格式，传输签名前请务必使用CDATA标签将JSON文本串保护起来。
            // goods_detail []：
            // └ goods_id String 必填 32 商品的编号
            // └ wxpay_goods_id String 可选 32 微信支付定义的统一商品编号
            // └ goods_name String 必填 256 商品名称
            // └ quantity Int 必填 商品数量
            // └ price Int 必填 商品单价，单位为分
            // └ goods_category String 可选 32 商品类目ID
            // └ body String 可选 1000 商品描述信息
            //	{
            //		"goods_detail":[
            //			{
            //				"goods_id":"iphone6s_16G",
            //				"wxpay_goods_id":"1001",
            //				"goods_name":"iPhone6s 16G",
            //				"quantity":1,
            //				"price":528800,
            //				"goods_category":"123456",
            //				"body":"苹果手机"
            //			},
            //			{
            //				"goods_id":"iphone6s_32G",
            //				"wxpay_goods_id":"1002",
            //				"goods_name":"iPhone6s 32G",
            //				"quantity":1,
            //				"price":608800,
            //				"goods_category":"123789",
            //				"body":"苹果手机"
            //			}
            //		]
            //	}
            if (StringUtils.isNotBlank(detail)) {
                params.put("detail", "<![CDATA[" + detail + "]]>");
            }
            // 附加数据
            // attach
            // 否
            // String(127)
            // 深圳分店
            // 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
            if (StringUtils.isNotBlank(attach)) {
                params.put("attach", attach);
            }
            // 商户订单号
            // out_trade_no
            // 是
            // String(32)
            // 20150806125346
            // 商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("out_trade_no", outTradeNo);
            // 货币类型
            // fee_type
            // 否
            // String(16)
            // CNY
            // 符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("fee_type", "CNY");
            // 总金额
            // total_fee
            // 是
            // Int
            // 888
            // 订单总金额，单位为分，详见支付金额
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("total_fee", totalFee);
            // 终端IP
            // spbill_create_ip
            // 是
            // String(16)
            // 123.12.12.123
            // APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
            params.put("spbill_create_ip", spbillCreateIp);
            // 交易起始时间
            // time_start
            // 否
            // String(14)
            // 20091225091010
            // 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("time_start", timeStart);
            // 交易结束时间
            // time_expire
            // 否
            // String(14)
            // 20091227091010
            // 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则
            // 注意：最短失效时间间隔必须大于5分钟
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("time_expire", timeExpire);
            // 商品标记
            // goods_tag
            // 否
            // String(32)
            // WXG	商品标记，代金券或立减优惠功能的参数，说明详见代金券或立减优惠
            // https://pay.weixin.qq.com/wiki/doc/api/tools/sp_coupon.php?chapter=12_1
            if (StringUtils.isNotBlank(goodsTag)) {
                params.put("goods_tag", goodsTag);
            }
            // 通知地址
            // notify_url
            // 是
            // String(256)
            // http://www.weixin.qq.com/wxpay/pay.php
            // 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
            params.put("notify_url",notify_url );
            // 交易类型
            // trade_type
            // 是
            // String(16)
            // JSAPI
            // 小程序取值如下：JSAPI，详细说明见参数规定
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("trade_type", "JSAPI");
            // 指定支付方式
            // limit_pay
            // 否
            // String(32)
            // no_credit
            // no_credit--指定不能使用信用卡支付
            if (limitPay) {
                params.put("limit_pay", "no_credit");
            }
            // 用户标识
            // openid
            // 否
            // String(128)
            // oUpF8uMuAJO_M2pxb1Q9zNjWeS6o
            // trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。openid如何获取，可参考【获取openid】。
            // https://mp.weixin.qq.com/debug/wxadoc/dev/api/api-login.html?t=20161122
            params.put("openid", openId);

            // 执行创建订单
            data = process(params, apiKey);
        } catch (Exception e) {
            log.error("签名失败，{}", e.getMessage());
        }
        return data;
    }

    /**
     * 创建订单-服务商
     *
     * @param appId          公众账号ID
     * @param mchId          商户号
     * @param subAppId       子商户公众账号ID
     * @param subMchId       子商户号
     * @param body           商品描述
     * @param detail         商品详情
     * @param attach         附加数据
     * @param outTradeNo     商户订单号
     * @param totalFee       总金额
     * @param spbillCreateIp 终端IP
     * @param timeStart      订单创建时间
     * @param timeExpire     订单失效时间
     * @param goodsTag       商品标记
     * @param productId      商品ID
     * @param limitPay       指定支付方式
     * @param subOpenId      用户子标识
     * @param apiKey         商户支付密钥
     * @param notify_url
     * @return 业务消息
     */
    public Map<String, Object> serviceProviderPay(String appId, String mchId, String subAppId, String subMchId, String body, String detail, String attach,
                                                  String outTradeNo, Integer totalFee, String spbillCreateIp, String timeStart, String timeExpire, String goodsTag,
                                                  String productId, boolean limitPay, String subOpenId, String apiKey, String notify_url) {
        Map<String, Object> data = null;
        try {
            TreeMap<String, Object> params = new TreeMap<>();
            // 公众账号ID
            // appid
            // 是
            // String(32)
            // wxd678efh567hg6787
            // 微信分配的公众账号ID
            params.put("appid", appId);
            // 商户号
            // mch_id
            // 是
            // String(32)
            // 1230000109
            // 微信支付分配的商户号
            params.put("mch_id", mchId);
            // 子商户公众账号ID
            // sub_appid
            // 是
            // String(32)
            // 1230000109
            // 微信分配的子商户公众账号ID，如需在支付完成后获取sub_openid则此参数必传。
            params.put("sub_appid", subAppId);
            // 子商户号
            // sub_mch_id
            // 是
            // String(32)
            // 1230000109
            // 微信支付分配的子商户号
            params.put("sub_mch_id", subMchId);
            // 设备号
            // device_info
            // 否
            // String(32)
            // 013467007045764
            // 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
            // params.put("device_info", "");
            // 随机字符串
            // nonce_str
            // 是
            // String(32)
            // 5K8264ILTKCH16CQ2502SI8ZNMTM67VS
            // 随机字符串，不长于32位。推荐随机数生成算法
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_3
            params.put("nonce_str", WxPayUtil.getRandomUpperStringByLength(32));
            // 签名类型
            // sign
            // 是
            // String(32)
            // C380BEC2BFD727A4B6845133519F3AD6
            // 签名，详见签名生成算法
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_3
            params.put("sign_type", "MD5");
            // 商品描述
            // body
            // 是
            // String(128)
            // 腾讯充值中心-QQ会员充值
            // 商品简单描述，该字段须严格按照规范传递，具体请见参数规定
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("body", body);
            // 商品详情
            // detail
            // 否
            // String(6000)
            // 商品详细列表，使用Json格式，传输签名前请务必使用CDATA标签将JSON文本串保护起来。
            // goods_detail []：
            // └ goods_id String 必填 32 商品的编号
            // └ wxpay_goods_id String 可选 32 微信支付定义的统一商品编号
            // └ goods_name String 必填 256 商品名称
            // └ quantity Int 必填 商品数量
            // └ price Int 必填 商品单价，单位为分
            // └ goods_category String 可选 32 商品类目ID
            // └ body String 可选 1000 商品描述信息
            //	{
            //		"goods_detail":[
            //			{
            //				"goods_id":"iphone6s_16G",
            //				"wxpay_goods_id":"1001",
            //				"goods_name":"iPhone6s 16G",
            //				"quantity":1,
            //				"price":528800,
            //				"goods_category":"123456",
            //				"body":"苹果手机"
            //			},
            //			{
            //				"goods_id":"iphone6s_32G",
            //				"wxpay_goods_id":"1002",
            //				"goods_name":"iPhone6s 32G",
            //				"quantity":1,
            //				"price":608800,
            //				"goods_category":"123789",
            //				"body":"苹果手机"
            //			}
            //		]
            //	}
            if (StringUtils.isNotBlank(detail)) {
                params.put("detail", "<![CDATA[" + detail + "]]>");
            }
            // 附加数据
            // attach
            // 否
            // String(127)
            // 深圳分店
            // 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
            if (StringUtils.isNotBlank(attach)) {
                params.put("attach", attach);
            }
            // 商户订单号
            // out_trade_no
            // 是
            // String(32)
            // 20150806125346
            // 商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("out_trade_no", outTradeNo);
            // 货币类型
            // fee_type
            // 否
            // String(16)
            // CNY
            // 符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("fee_type", "CNY");
            // 总金额
            // total_fee
            // 是
            // Int
            // 888
            // 订单总金额，单位为分，详见支付金额
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("total_fee", totalFee);
            // 终端IP
            // spbill_create_ip
            // 是
            // String(16)
            // 123.12.12.123
            // APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
            params.put("spbill_create_ip", spbillCreateIp);
            // 交易起始时间
            // time_start
            // 否
            // String(14)
            // 20091225091010
            // 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("time_start", timeStart);
            // 交易结束时间
            // time_expire
            // 否
            // String(14)
            // 20091227091010
            // 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则
            // 注意：最短失效时间间隔必须大于5分钟
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("time_expire", timeExpire);
            // 商品标记
            // goods_tag
            // 否
            // String(32)
            // WXG	商品标记，代金券或立减优惠功能的参数，说明详见代金券或立减优惠
            // https://pay.weixin.qq.com/wiki/doc/api/tools/sp_coupon.php?chapter=12_1
            if (StringUtils.isNotBlank(goodsTag)) {
                params.put("goods_tag", goodsTag);
            }
            // 通知地址
            // notify_url
            // 是
            // String(256)
            // http://www.weixin.qq.com/wxpay/pay.php
            // 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
            params.put("notify_url", notify_url);
            // 交易类型
            // trade_type
            // 是
            // String(16)
            // JSAPI
            // 小程序取值如下：JSAPI，详细说明见参数规定
            // https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=4_2
            params.put("trade_type", "JSAPI");
            // 商品ID
            // product_id
            // 否
            // String(32)
            // 12235413214070356458058
            // trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。
            if (StringUtils.isNotBlank(productId)) {
                params.put("product_id", productId);
            }
            // 指定支付方式
            // limit_pay
            // 否
            // String(32)
            // no_credit
            // no_credit--指定不能使用信用卡支付
            if (limitPay) {
                params.put("limit_pay", "no_credit");
            }
            // 用户标识
            // openid
            // 否
            // String(128)
            // oUpF8uMuAJO_M2pxb1Q9zNjWeS6o
            // trade_type=JSAPI，此参数必传，用户在主商户appid下的唯一标识。openid和sub_openid可以选传其中之一，如果选择传sub_openid,则必须传sub_appid。下单前需要调用【网页授权获取用户信息】接口获取到用户的Openid。
            // http://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html
//            params.put("openid", openId);
            // 用户子标识
            // sub_openid
            // 否
            // String(128)
            // oUpF8uMuAJO_M2pxb1Q9zNjWeS6o
            // trade_type=JSAPI，此参数必传，用户在主商户appid下的唯一标识。openid和sub_openid可以选传其中之一，如果选择传sub_openid,则必须传sub_appid。下单前需要调用【网页授权获取用户信息】接口获取到用户的Openid。
            // http://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html
            params.put("sub_openid", subOpenId);

            // 执行创建订单
            data = process(params, apiKey);
        } catch (Exception e) {
            log.error("签名失败，{}", e.getMessage());
        }
        return data;
    }

    /**
     * 创建预支付订单
     *
     * @param params 参数
     * @param apiKey 密钥
     * @return 创建结果
     */
    private Map<String, Object> process(TreeMap<String, Object> params, String apiKey) {
        Map<String, Object> data = new HashMap<>();
        try {
            String sign = WxPayUtil.createSign(params, apiKey, "UTF-8");

            //检测签名结果
            if (StringUtils.isNotBlank(sign)) {
                log.debug("签名字符串：{}", sign);
                params.put("sign", sign);

                XmlMapper xmlMapper = new XmlMapper();
                ObjectWriter writer = xmlMapper.writer().withRootName("xml");

                byte[] postData = writer.writeValueAsBytes(params);
                log.debug("提交参数：{}", new String(postData, StandardCharsets.UTF_8));

                String result = Request.Post(WxApi.WX_PRE_ORDER_URL)
                        .bodyByteArray(postData, ContentType.APPLICATION_XML)
                        .execute()
                        .returnContent()
                        .asString(StandardCharsets.UTF_8);

                log.debug("响应结果：{}", result);

                //处理返回结果
                String temp = result.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", "");

                log.debug("响应结果：{}", temp);

                data = xmlMapper.readValue(temp, new TypeReference<Map<String, String>>() {
                });
                if (data.containsKey("prepay_id")) {

                    // 设置ApiKey
                    params.put("apikey", apiKey);

                    // 设置请求包
                    data.put("request_tag", writer.writeValueAsString(params));

                    // 设置响应包
                    data.put("return_tag", temp);
                } else {
                    log.error("创建订单失败，{}", temp);
                }
            }
        } catch (Exception e) {
            log.error("签名失败，{}", e.getMessage());
        }
        return data;
    }


}