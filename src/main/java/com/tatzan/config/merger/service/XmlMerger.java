package com.tatzan.config.merger.service;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface XmlMerger {

    File mergeXml(List<Object> elements, String outputFileName) throws IOException, SAXException, TransformerException, ParserConfigurationException;

    String mergeXml(List<Object> elements) throws IOException, ParserConfigurationException, SAXException, TransformerException;

}
