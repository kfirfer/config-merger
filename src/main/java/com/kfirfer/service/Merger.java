package com.kfirfer.service;

import com.kfirfer.model.ConfigMetadata;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Merger {

    List<File> merge(List<ConfigMetadata> configMetadataList) throws IOException, XMLStreamException, ParserConfigurationException, TransformerException, SAXException;

}
