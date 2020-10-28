package com.tatzan.config.merger.service.impl;

import com.tatzan.config.merger.service.JsonMerger;
import com.tatzan.config.merger.util.JsonUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JsonMergerImpl implements JsonMerger {

    private final JSONParser parser = new JSONParser();

    @Override
    public File mergeJson(List<Object> elements, String outputFileName) throws ParseException, IOException {
        Path outputFile = Paths.get(outputFileName);
        JSONObject jsonObject = getJsonObject(elements);
        try (FileWriter fileWriter = new FileWriter(outputFile.toFile())) {
            fileWriter.write(jsonObject.toString());
        }
        return outputFile.toFile();
    }


    @Override
    public String mergeJson(List<Object> elements) throws ParseException, IOException {
        JSONObject jsonObject = getJsonObject(elements);
        return jsonObject.toString();
    }


    private JSONObject getJsonObject(List<Object> elements) throws ParseException, IOException {
        JSONObject jsonObject = new JSONObject();
        for (Object json : elements) {
            JSONObject inputJsonObject;
            if (json instanceof String) {
                inputJsonObject = (JSONObject) parser.parse((String) json);
            } else {
                inputJsonObject = (JSONObject) parser.parse(new FileReader((File) json));//path to the JSON file.
            }
            jsonObject = JsonUtils.deepMerge(inputJsonObject, jsonObject);
        }
        return jsonObject;
    }

}
