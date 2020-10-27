package com.kfirfer.util;

import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class MapUtils {

    public static Map<String, Object> deepMerge(Map<String, Object> source, Map<String, Object> target) {
        source.keySet().forEach(key ->
        {
            Object value = source.get(key);
            if (!target.containsKey(key)) {
                // new value for "key":
                target.put(key, value);
            } else {
                // existing value for "key" - recursively deep merge:
                if (value instanceof Map) {
                    Map valueJson = (Map) value;
                    deepMerge(valueJson, (JSONObject) target.get(key));
                } else if (value instanceof List) {
                    ((List) value).forEach(
                            jsonobj ->
                                    ((List) target.get(key)).add(jsonobj));

                } else {
                    target.put(key, value);
                }
            }
        });
        return target;
    }

}
