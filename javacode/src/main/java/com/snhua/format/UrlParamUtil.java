package com.snhua.format;

import java.net.URLEncoder;
import java.util.Map;

public class UrlParamUtil {

    /**
     * 将map转换成url参数形式 a=1&b=1
     *
     * @param mapParam
     * @return
     */
    public static String mapToUrlParam(Map<String, Object> mapParam, boolean encode) {
        if (mapParam == null || mapParam.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (String key : mapParam.keySet()) {
            i++;
            if (i > 1) {
                sb.append("&");
            }
            if (encode) {
                try {
                    sb.append(key + "=" + URLEncoder.encode((String) mapParam.get(key)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(key + "=" + mapParam.get(key));
            }
        }
        return sb.toString();
    }

}
