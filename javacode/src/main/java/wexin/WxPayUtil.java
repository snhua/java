package wexin;


import com.snhua.encryption.md5.MD5Util;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Random;
import java.util.SortedMap;

/**
 * Created by SongpoLiu on 2017/6/2.
 */
public class WxPayUtil {

    /**
     * 生成签名
     *
     * @param params
     * @return
     */
    public static String createSign(SortedMap<String, Object> params, String apiKey, String charset) {
        //将map中的参数转换成带有&的字符串
        LinkedList<String> paramList = new LinkedList<>();
        params.forEach((k, v) -> {
            if (null != v && !"".equals(v)) {
                paramList.add(k + "=" + v);
            }
        });
        String waitSignStr = String.join("&", paramList);

        //拼接API密钥：
        String stringSignTemp = waitSignStr + "&key=" + apiKey;
        //签名
        return MD5Util.MD5Encode(stringSignTemp, charset).toUpperCase();
    }

    /**
     * 生成签名
     *
     * @param params
     * @return
     */
    public static String createSign(SortedMap<String, Object> params, String apiKey) {
        return createSign(params, apiKey, "UTF-8");
    }


    /**
     * 签名校验
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("rawtypes")
    public static boolean verifyPaySign(SortedMap paras, String sign, String appKey, String charset) {
        String paySign = WxPayUtil.createSign(paras, appKey, charset);
        return !StringUtils.isEmpty(paySign) && paySign.equalsIgnoreCase(sign);
    }

    /**
     * 签名校验
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("rawtypes")
    public static boolean verifyPaySign(SortedMap paras, String sign, String appKey) {
        return verifyPaySign(paras, sign, appKey, "UTF-8");
    }

    /**
     * 生成指定长度大写随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String getRandomUpperStringByLength(int length) {
        return getRandomStringByLength(length).toUpperCase();
    }

    /**
     * 生成指定长度小写写随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String getRandomLowerStringByLength(int length) {
        return getRandomStringByLength(length);
    }

    private static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 返回微信支付回调结果
     *
     * @param returnCode 代码
     * @param returnMsg  消息
     * @return 回调结果
     */
    public static String generateReturnData(String returnCode, String returnMsg) {
        return "<xml>" +
                "<return_code><![CDATA[" + returnCode + "]]></return_code>" +
                "<return_msg><![CDATA[" + returnMsg + "]]></return_msg>" +
                "</xml>";
    }

}
