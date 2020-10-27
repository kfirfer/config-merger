package com.tatzan.config.merger.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonUtils {

    public static JSONObject deepMerge(JSONObject source, JSONObject target) {
        source.keySet().forEach(key ->
        {
            Object value = source.get(key);
            if (!target.containsKey(key)) {
                // new value for "key":
                target.put(key, value);
            } else {
                // existing value for "key" - recursively deep merge:
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject) value;
                    deepMerge(valueJson, (JSONObject) target.get(key));
                } else if (value instanceof JSONArray) {
                    ((JSONArray) value).forEach(
                            jsonobj ->
                                    ((JSONArray) target.get(key)).add(jsonobj));

                } else {
                    target.put(key, value);
                }
            }
        });
        return target;
    }

}
