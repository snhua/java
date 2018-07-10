package com.snhua.encryption;

import com.snhua.encryption.rsa.common.RSAUtil;
import com.snhua.time.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.Base64;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
public class RsaTest {
    /**
     * 创建公私密钥对
     *
     * @throws Exception
     */
    @Test
    public void createRsaKey() throws Exception {
        // 生成一对公私钥，路径可以自己填
        final String nowds = TimeUtil.timeString();
        final String publicFilePath = "/data/temp/RSA/public-" + nowds + ".key";
        final String privateFilePath = "/data/temp/RSA/private-" + nowds + ".key";
        RSAUtil.createKey(publicFilePath, privateFilePath, 1024);
        // 转换为公钥对象
        PublicKey pubKey = RSAUtil.resolvePublicKey(publicFilePath);
        // 转换为私钥对象
        PrivateKey priKey = RSAUtil.resolvePrivateKey(privateFilePath);
        // Base64编码后的公钥字符串
        final String public_rsa = Base64.encodeBase64String(pubKey.getEncoded());//.replaceAll("\r\n","");
        System.out.println(public_rsa);
        // Base64编码后的私钥字符串
        final String private_rsa = Base64.encodeBase64String(priKey.getEncoded());//.replaceAll("\r\n","");
        System.out.println(private_rsa);
        String data = "B64DC35297E509D8078FDD64DDBBED73";
        // RSA加签
        priKey = RSAUtil.getPrivateKey(private_rsa);
        String signData = RSAUtil.sign(data, priKey);
        System.out.println("签名值为：" + signData);
        // RSA验签
        pubKey = RSAUtil.getPublicKey(public_rsa);
        boolean result = RSAUtil.vertiy(data, signData, pubKey);
        System.out.println("验签结果为：" + result);
//        log.debug(String.format("<p>public key:</p><p>%s</p><p>private key:</p><p>%s</p>", public_rsa, private_rsa));
    }
}
