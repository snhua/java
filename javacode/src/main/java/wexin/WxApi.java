package wexin;

/**
 * Created by SongpoLiu on 2017/6/2.
 */
public class WxApi {

    // 小程序二维码-限制数量
    public static final String WX_QR_CODE_LIMIT_URL = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode";

    // 小程序码-限制数量
    public static final String WE_CHAT_QR_CODE_LIMIT_URL = "https://api.weixin.qq.com/wxa/getwxacode";

    // 小程序码-不限数量
    public static final String WE_CHAT_QR_CODE_UN_LIMIT_URL = "http://api.weixin.qq.com/wxa/getwxacodeunlimit";

    // 微信Token地址
    public static final String WX_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    // 微信预下单地址
    public static final String WX_PRE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    // 微信支付结果检验地址
    public static final String WX_VERIFY_PAY_RESULT_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

    // 微信企业付款地址
    public static final String WX_ENTERPRISE_PAY_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    //微信查询企业付款
    public static final String WX_PAY_RESULT_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo";

}
