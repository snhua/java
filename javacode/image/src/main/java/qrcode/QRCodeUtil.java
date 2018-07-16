package qrcode;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.snhua.time.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import wexin.WeChatPublicUtil;
import wexin.WxApi;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Random;

/**
 * Created by Administrator on 2017/9/15 0015.
 */
@Slf4j
public class QRCodeUtil {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 798;
    // LOGO宽度
    private static final int WIDTH = 180;
    // LOGO高度
    private static final int HEIGHT = 180;


    public static BufferedImage createImage(String content) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
//        if (inputStream == null) {
//            return image;
//        }
//        // 插入图片
//        QRCodeUtil.insertImage(image, inputStream, needCompress);
        return image;
    }


    public byte[] BufferedImageToBytes(BufferedImage image, Integer width, Integer height, String imgUrl, boolean needCompress) {
        byte[] bytes;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(width * height)){
            Thumbnails.of(image)
                    .size(width, height)
                    .outputFormat("jpg")
                    .outputQuality(1.0f)
                    .toOutputStream(baos);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            log.error("生成二维码带logo失败", e);
            return null;
        }
        return bytes;
    }

    /**
     * 插入LOGO
     *
     * @param source         二维码图片
     * @param imgInputStream LOGO图片流
     * @param needCompress   是否压缩
     * @throws Exception
     */
    public static void insertImage(BufferedImage source, InputStream imgInputStream,
                                   boolean needCompress) throws Exception {
        if (imgInputStream == null) {
            return;
        }
        Image src = ImageIO.read(imgInputStream);
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
        imgInputStream.close();
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content        内容
     * @param imgInputStream LOGO地址
     * @param destPath       存放目录
     * @param needCompress   是否压缩LOGO
     * @throws Exception
     */
    public static void encode(String content, InputStream imgInputStream, String destPath,
                              boolean needCompress) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content);
        insertImage(image,imgInputStream,needCompress);
        mkdirs(destPath);
        String file = new Random().nextInt(99999999) + ".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
    }

    /**
     * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
     *
     * @param destPath 存放目录
     * @author lanyuan
     * Email: mmm333zzz520@163.com
     * @date 2013-12-11 上午10:16:36
     */
    public static void mkdirs(String destPath) {
        File file = new File(destPath);
        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content        内容
     * @param imgInputStream LOGO地址
     * @param destPath       存储地址
     * @throws Exception
     */
    public static void encode(String content, InputStream imgInputStream, String destPath)
            throws Exception {
        QRCodeUtil.encode(content, imgInputStream, destPath, false);
    }

    /**
     * 生成二维码
     *
     * @param content      内容
     * @param destPath     存储地址
     * @param needCompress 是否压缩LOGO
     * @throws Exception
     */
    public static void encode(String content, String destPath,
                              boolean needCompress) throws Exception {
        QRCodeUtil.encode(content, null, destPath, needCompress);
    }

    /**
     * 生成二维码
     *
     * @param content  内容
     * @param destPath 存储地址
     * @throws Exception
     */
    public static void encode(String content, String destPath) throws Exception {
        QRCodeUtil.encode(content, null, destPath, false);
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content        内容
     * @param imgInputStream LOGO地址
     * @param output         输出流
     * @param needCompress   是否压缩LOGO
     * @throws Exception
     */
    public static void encode(String content, InputStream imgInputStream,
                              OutputStream output, boolean needCompress) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content);
        insertImage(image,imgInputStream,needCompress);
        ImageIO.write(image, FORMAT_NAME, output);
    }

    /**
     * 生成二维码
     *
     * @param content 内容
     * @param output  输出流
     * @throws Exception
     */
    public static void encode(String content, OutputStream output)
            throws Exception {
        QRCodeUtil.encode(content, null, output, false);
    }


    /**
     * 生成微信小程序二维码
     * @param page
     * @param sence
     * @param appId
     * @param secret
     * @param width
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public byte[] createWXApplicationQrcode(String page, String sence, String appId,String secret, int width) throws URISyntaxException, IOException {



            if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(secret)) {
                return null;
            }
            String accessToken =WeChatPublicUtil.getAccessToken(appId, secret);
            if (!StringUtils.isEmpty(accessToken)) {
                URIBuilder uriBuilder = new URIBuilder(WxApi.WE_CHAT_QR_CODE_UN_LIMIT_URL);

                uriBuilder.addParameter("access_token", accessToken);

                JSONObject paramJson = new JSONObject();
                paramJson.put("scene", sence == null ? "t=" + TimeUtil.nowTimestamp() : sence);

                paramJson.put("page", page);
                paramJson.put("width", width);

                Content content = Request.Post(uriBuilder.build())
                        .bodyString(paramJson.toJSONString(), ContentType.APPLICATION_JSON)
                        .execute().returnContent();

                return content.asBytes();
            }
        return null;
    }


    public static void main(String[] args) throws Exception {
        String text = "http://www.baidu.com";
//        QRCodeUtil.encode(text, ImageRequest("http://img03.sogoucdn.com/app/a/100520020/40f367c9a23d664e5981db6fe9c2b9d1"), "c:/a/", true);
    }

}
