package com.tatzan.config.merger.service.impl;

import com.tatzan.config.merger.exception.DuplicateFileTypeException;
import com.tatzan.config.merger.exception.DuplicateInputException;
import com.tatzan.config.merger.model.ConfigMetadata;
import com.tatzan.config.merger.model.ConfigResult;
import com.tatzan.config.merger.model.FileType;
import com.tatzan.config.merger.service.*;
import com.tatzan.config.merger.util.MapUtils;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ConfigMergerImpl implements ConfigMerger {

    private final XmlMerger xmlMerger = new XmlMergerImpl();
    private final JsonMerger jsonMerger = new JsonMergerImpl();
    private final YmlMerger ymlMerger = new YmlMergerImpl();
    private final PropertiesMerger propertiesMerger = new PropertiesMergerImpl();

    @Override
    public List<ConfigResult> merge(List<ConfigMetadata> configMetadataList, boolean writeFile) throws IOException, ParserConfigurationException, TransformerException, SAXException, ParseException {
        Map<String, List<ConfigMetadata>> filesMap = new HashMap<>();
        Map<String, FileType> tempTypes = new HashMap<>();

        for (ConfigMetadata configMetadata : configMetadataList) {
            if (!filesMap.containsKey(configMetadata.getOutputFile())) {
                filesMap.put(configMetadata.getOutputFile(), new ArrayList<>());
                tempTypes.put(configMetadata.getOutputFile(), configMetadata.getFileType());
            }
            this.validations(tempTypes, configMetadata);
            List<ConfigMetadata> filesList = filesMap.get(configMetadata.getOutputFile());
            filesList.add(configMetadata);
        }

        return mergeConfigs(filesMap, writeFile);
    }

    private void validations(Map<String, FileType> tempTypes, ConfigMetadata configMetadata) {
        if (tempTypes.get(configMetadata.getOutputFile()) != configMetadata.getFileType()) {
            throw new DuplicateFileTypeException("Only one type of config file is allowed");
        }
        if (configMetadata.getInputFilePath() != null && configMetadata.getInputString() != null) {
            throw new DuplicateInputException("Only one input type allowed (string/filepath)");
        }
    }

    @Override
    public Map<String, Object> mergeMaps(List<Map<String, Object>> mapList) {
        Map<String, Object> outputMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            outputMap = MapUtils.deepMerge(outputMap, map);
        }
        return outputMap;
    }

    private List<ConfigResult> mergeConfigs(Map<String, List<ConfigMetadata>> configMaps, boolean writeFile) throws IOException, ParserConfigurationException, TransformerException, SAXException, ParseException {
        Iterator iterator = configMaps.entrySet().iterator();
        List<ConfigResult> filesList = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            filesList.add(mergeConfig((String) pair.getKey(), (List<ConfigMetadata>) pair.getValue(), writeFile));
            iterator.remove();
        }
        return filesList;
    }

    private ConfigResult mergeConfig(String outputFileName, List<ConfigMetadata> configMetadataList, boolean writeFile) throws IOException, ParserConfigurationException, TransformerException, SAXException, ParseException {
        ConfigResult configResult = new ConfigResult();
        List<Object> elements = new ArrayList<>();

        for (ConfigMetadata configMetadata : configMetadataList) {
            if (configMetadata.getInputFilePath() != null) {
                elements.add(new File(configMetadata.getInputFilePath()));
            } else {
                elements.add(configMetadata.getInputString());
            }
        }

        ConfigMetadata firstElement = configMetadataList.get(0);
        String content = "";

        if (firstElement.getFileType() == FileType.XML) {
            content = xmlMerger.mergeXml(elements);
        }
        if (firstElement.getFileType() == FileType.JSON) {
            content = jsonMerger.mergeJson(elements);
        }
        if (firstElement.getFileType() == FileType.YAML) {
            content = ymlMerger.mergeYaml(elements);
        }
        if (firstElement.getFileType() == FileType.PROPERTIES) {
            content = propertiesMerger.mergeProperties(elements);
        }

        if (writeFile) {
            File file = new File(outputFileName);
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(content);
            }
        }

        configResult.setOutputFilePath(outputFileName);
        configResult.setContent(content);
        return configResult;
    }


}
