package com.tatzan.config.merger.service;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface JsonMerger {

    File mergeJson(List<Object> elements, String outputFileName) throws ParseException, IOException;

    String mergeJson(List<Object> elements) throws ParseException, IOException;

}
