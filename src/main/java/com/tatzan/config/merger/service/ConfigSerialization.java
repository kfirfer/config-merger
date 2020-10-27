package com.tatzan.config.merger.service;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public interface ConfigSerialization {

    File mapToJson(Map<String, Object> map, String outputFilePath) throws FileNotFoundException;

    File mapToYaml(Map<String, Object> map, String outputFilePath) throws FileNotFoundException;

    File mapToXml(Map<String, Object> map, String outputFilePath, String rootElement) throws FileNotFoundException, JAXBException;

    Map<String, Object> jsonFileToMap(File inputFile) throws IOException;

    Map<String, Object> yamlFileToMap(File inputFile) throws IOException;

    Object xmlFileToObject(File inputFile) throws IOException, JAXBException, ParserConfigurationException, TransformerException, SAXException;

}
