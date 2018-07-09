package com.snhua.format;

import java.util.Map;

public class XmlUtil {
    private static final String FORMAT_XML = "<%s><![CDATA[%s]]></%s>";

    /**
     * map转换为xml格式
     * @param mapParam
     * @return
     */
    public static String mapToXml(Map<String, String> mapParam) {
        StringBuilder builder = new StringBuilder("<xml>");
        for (String key : mapParam.keySet()) {

            builder.append(String.format(FORMAT_XML, key, mapParam.get(key), key));
        }
        builder.append("</xml>");
        return builder.toString();
    }
}
