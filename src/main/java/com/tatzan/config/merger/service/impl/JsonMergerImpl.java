package com.tatzan.config.merger.service.impl;

import com.tatzan.config.merger.exception.UnsupportedObjectException;
import com.tatzan.config.merger.service.JsonMerger;
import com.tatzan.config.merger.util.JsonUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class JsonMergerImpl implements JsonMerger {

    private final JSONParser parser = new JSONParser();

    @Override
    public String mergeJsonFiles(List<File> jsonFiles) throws ParseException, IOException {
        JSONObject jsonObject = getJsonObject(jsonFiles);
        return jsonObject.toString();
    }

    @Override
    public String mergeJsonStrings(List<String> elements) throws ParseException, IOException {
        JSONObject jsonObject = getJsonObject(elements);
        return jsonObject.toString();
    }

    @Override
    public String mergeJson(List<Object> jsons) throws ParseException, IOException {
        JSONObject jsonObject = getJsonObject(jsons);
        return jsonObject.toString();
    }

    private JSONObject getJsonObject(Object elements) throws ParseException, IOException {
        JSONObject jsonObject = new JSONObject();
        List<Object> elementList = (List<Object>) elements;
        for (Object json : elementList) {
            System.out.println(json.getClass().getName());
            JSONObject inputJsonObject;
            if (json instanceof String) {
                inputJsonObject = (JSONObject) parser.parse((String) json);
            } else if (json instanceof File) {
                inputJsonObject = (JSONObject) parser.parse(new FileReader((File) json));//path to the JSON file.
            } else {
                throw new UnsupportedObjectException("Only File/String objects are supported");
            }
            jsonObject = JsonUtils.deepMerge(inputJsonObject, jsonObject);
        }
        return jsonObject;
    }

}
