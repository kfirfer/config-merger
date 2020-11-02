package com.tatzan.config.merger.service.impl;

import com.tatzan.config.merger.exception.UnsupportedObjectException;
import com.tatzan.config.merger.service.PropertiesMerger;
import com.tatzan.config.merger.util.PropertiesUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesMergerImpl implements PropertiesMerger {

    @Override
    public String merge(List<Properties> propertiesConfigs) {
        Properties merged = new Properties();
        for (Properties properties : propertiesConfigs) {
            merged.putAll(properties);
        }
        return PropertiesUtils.getPropertyAsString(merged);
    }

    @Override
    public String mergeProperties(List<Object> elements) throws IOException {
        return this.mergePropertiesAndPropertiesObject(elements);
    }

    public String mergePropertiesAndPropertiesObject(Object elements) throws IOException {
        List<Object> elementList = (List<Object>) elements;
        List<Properties> propertiesConfigs = new ArrayList<>();
        for (Object properties : elementList) {
            Properties inputProperties;
            if (properties instanceof String) {
                inputProperties = PropertiesUtils.parsePropertiesString((String) properties);
            } else if (properties instanceof File) {
                inputProperties = PropertiesUtils.parsePropertiesString(Files.readString(((File) properties).toPath(), StandardCharsets.UTF_8));
            } else {
                throw new UnsupportedObjectException("Only File/String objects are supported");
            }
            propertiesConfigs.add(inputProperties);
        }
        return this.merge(propertiesConfigs);
    }


}
