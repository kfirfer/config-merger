package com.tatzan.config.merger.service;

import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public interface PropertiesMerger {

    String merge(List<Properties> propertiesConfigs) throws IOException, ParserConfigurationException, SAXException, TransformerException;

    String mergeProperties(List<Object> elements) throws IOException, ParseException;

}
