package com.tatzan.config.merger.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface JsonMerger {

    String mergeJsonStrings(List<String> jsonStrings) throws ParseException, IOException;

    String mergeJsonFiles(List<File> jsonFiles) throws ParseException, IOException;

    String mergeJson(List<Object> jsonStrings) throws ParseException, IOException;

    JSONObject mergeJsonAndGetJsonObject(Object elements) throws IOException, ParseException;

}
