package com.tatzan.config.merger.service;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface XmlMerger {

    String mergeXmlStrings(List<String> elements) throws IOException, ParserConfigurationException, SAXException, TransformerException;

    String mergeXmlFiles(List<File> elements) throws IOException, ParserConfigurationException, SAXException, TransformerException;

    String mergeXml(Object elements) throws IOException, ParserConfigurationException, SAXException, TransformerException;

}
