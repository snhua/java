package wexin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import wexin.domain.AuthToken;
import wexin.domain.AuthUser;
import wexin.domain.JsTicket;
import wexin.domain.JsToken;

import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

@Slf4j
public class WeChatPublicUtil {

    /**
     * 获取授权令牌
     * {
     * "access_token": "fqva067m6rklp4KyGS1cdUbolrN20oDWsB64pHIC5H6EztNUlDuHkQoQLPl5bEfoe21pVohLw73ZVJaiUYBRqg",
     * "expires_in": 7200,
     * "refresh_token": "AltsDNpWAkOcAmR7GpaXuUL9wLm9snph8flm9CBxZOJFWjmCTptPyMC4UlzeOSEQhtoRbhtIyWoJNtmprOMCAQ",
     * "openid": "oTJXAwCPs7nTzeY8K8ybnzHUdpY8",
     * "scope": "snsapi_userinfo"
     * }
     *
     * @param appId
     * @param state
     * @param code
     * @return
     */
    public static AuthToken getAuthToken(String appId, String secret, String state, String code) {
        log.debug("获取授权令牌：appId = [" + appId + "], secret = [" + secret + "], state = [" + state + "], code = [" + code + "]");
        AuthToken token = new AuthToken();
        try {
            String uri = "https://api.weixin.qq.com/sns/oauth2/access_token";

            URIBuilder builder = new URIBuilder(uri);
            builder.addParameter("appid", appId);
            builder.addParameter("secret", secret);
            builder.addParameter("code", code);
            builder.addParameter("grant_type", "authorization_code");

            HttpClient client = HttpClients.createDefault();
            HttpEntity entity = client.execute(new HttpGet(builder.toString())).getEntity();
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            log.debug("响应信息：{}", result);

            if (StringUtils.isNotBlank(result)) {
                token = new ObjectMapper().readValue(result, AuthToken.class);
            }
        } catch (Exception e) {
            log.error("获取授权信息失败", e);
        }
        return token;
    }

    /**
     * @param accessToken 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     * @param openId      用户的唯一标识
     * @param lang        返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
     * @return
     */
    public static AuthUser getAuthUser(String accessToken, String openId, String lang) {
        log.debug("获取用户信息：accessToken = [" + accessToken + "], openId = [" + openId + "], lang = [" + lang + "]");
        AuthUser authUser = new AuthUser();
        try {
            String uri = "https://api.weixin.qq.com/sns/userinfo";

            URIBuilder builder = new URIBuilder(uri);
            builder.addParameter("access_token", accessToken);
            builder.addParameter("openid", openId);
            builder.addParameter("lang", lang);

            HttpClient client = HttpClients.createDefault();
            HttpEntity entity = client.execute(new HttpGet(builder.toString())).getEntity();
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            log.debug("响应信息：{}", result);

            if (StringUtils.isNotBlank(result)) {
                authUser = new ObjectMapper().readValue(result, AuthUser.class);
            }
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
        }
        return authUser;
    }

    public static JsToken getWeChatJsAccessToken(String appId, String secret) {
        log.debug("获取微信js授权令牌：appId = [" + appId + "], secret = [" + secret + "]");
        JsToken token = new JsToken();
        try {
            String uri = "https://api.weixin.qq.com/cgi-bin/token";
            URIBuilder builder = new URIBuilder(uri);
            builder.addParameter("appid", appId);
            builder.addParameter("secret", secret);
            builder.addParameter("grant_type", "client_credential");

            HttpClient client = HttpClients.createDefault();
            HttpEntity entity = client.execute(new HttpGet(builder.toString())).getEntity();
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            log.debug("响应信息：{}", result);

            if (StringUtils.isNotBlank(result)) {
                token = new ObjectMapper().readValue(result, JsToken.class);
            }
        } catch (Exception e) {
            log.error("获取微信js授权令牌失败", e);
        }
        return token;
    }

    public static JsTicket getWechatJsTicket(String accessToken) {
        log.debug("获取微信js票据：accessToken = [" + accessToken + "]");
        JsTicket ticket = new JsTicket();
        try {
            String uri = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
            URIBuilder builder = new URIBuilder(uri);
            builder.addParameter("access_token", accessToken);
            builder.addParameter("type", "jsapi");

            HttpClient client = HttpClients.createDefault();
            HttpEntity entity = client.execute(new HttpGet(builder.toString())).getEntity();
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            log.debug("响应信息：{}", result);

            if (StringUtils.isNotBlank(result)) {
                ticket = new ObjectMapper().readValue(result, JsTicket.class);
            }
        } catch (Exception e) {
            log.error("获取微信js票据失败", e);
        }
        return ticket;
    }

    public static String getWechatJsSign(TreeMap<String, Object> params) throws DigestException {
        log.debug("生成微信JS签名，{}", params);
        StringBuilder builder = new StringBuilder();
        params.forEach((k, v) -> builder.append(k).append("=").append(v).append("&"));

        //获取信息摘要 - 参数字典排序后字符串
        String decrypt = builder.substring(0, builder.length() - 1);
        log.debug("排序后的字符串：{}", decrypt);
        try {
            //指定sha1算法
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decrypt.getBytes());
            //获取字节数组
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            String sign = hexString.toString().toUpperCase();
            log.debug("生成微信JS签名成功：{}", sign);
            return sign;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new DigestException("生成微信JS签名错误！");
        }
    }

    /**
     * 调用微信接口获取 openId
     *
     * @param wcode  微信授权返回code
     * @param appId  公众号Appid
     * @param secret 公众号 secret
     * @return
     */
    public String getOpenIdByWcode(String wcode, String appId, String secret) {
        AuthToken token = WeChatPublicUtil.getAuthToken(appId, secret, "", wcode);
        return token.getOpenid();
    }
}
