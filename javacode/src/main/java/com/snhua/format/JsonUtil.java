package com.snhua.format;

import com.alibaba.fastjson.JSON;
import java.util.Map;

public class JsonUtil {



    public static String map2Json(Map map) {
        String result = "";
        if (null != map && map.size() > 0) {
            result = JSON.toJSONString(map);
        }
        return result;
    }
}
