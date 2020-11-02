package com.tatzan.config.merger.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtils {

    public static Properties parsePropertiesString(String s) throws IOException {
        // grr at load() returning void rather than the Properties object
        // so this takes 3 lines instead of "return new Properties().load(...);"
        final Properties p = new Properties();
        p.load(new StringReader(s));
        return p;
    }

    public static String getPropertyAsString(Properties properties) {
        StringBuilder stringBuilder = new StringBuilder(100);
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            stringBuilder.append(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        return stringBuilder.toString();
    }
}
