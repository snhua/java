package com.snhua.format;

import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.Map;

public class UrlParamUtil {

    /**
     * 将map转换成url参数形式 a=1&b=1
     *
     * @param mapParam
     * @return
     */
    public static String mapToUrlParam(Map<String, Object> mapParam, boolean isEncode, String chartSet) {
        if (mapParam == null || mapParam.isEmpty()) {
            return "";
        }
        if (isEncode) {
            if (StringUtils.isEmpty(chartSet)) {
                chartSet = "utf-8";
            }
        }
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (String key : mapParam.keySet()) {
            i++;
            if (i > 1) {
                sb.append("&");
            }
            if (isEncode) {
                try {
                    sb.append(key + "=" + URLEncoder.encode((String) mapParam.get(key), chartSet));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(key + "=" + mapParam.get(key));
            }
        }
        return sb.toString();
    }

    public static String mapToUrlParam(Map<String, Object> mapParam) {
        return mapToUrlParam(mapParam, false, null);
    }

    public static String mapToUrlParam(Map<String, Object> mapParam, String charSet) {
        return mapToUrlParam(mapParam, true, charSet);
    }

    public static String mapToUrlParam(Map<String, Object> mapParam, boolean isEncode) {
        return mapToUrlParam(mapParam, isEncode, null);
    }
}
