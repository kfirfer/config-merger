package com.tatzan.config.merger.service;

import com.tatzan.config.merger.model.ConfigMetadata;
import com.tatzan.config.merger.model.ConfigResult;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ConfigMerger {

    List<ConfigResult> merge(List<ConfigMetadata> configMetadataList, boolean writeFile) throws IOException, XMLStreamException, ParserConfigurationException, TransformerException, SAXException, ParseException;

    Map<String, Object> mergeMaps(List<Map<String, Object>> mapList);


}
