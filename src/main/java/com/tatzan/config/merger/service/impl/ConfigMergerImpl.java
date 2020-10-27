package com.tatzan.config.merger.service.impl;

import com.tatzan.config.merger.exception.DuplicateFileTypeException;
import com.tatzan.config.merger.exception.DuplicateInputException;
import com.tatzan.config.merger.model.ConfigMetadata;
import com.tatzan.config.merger.model.FileType;
import com.tatzan.config.merger.service.ConfigMerger;
import com.tatzan.config.merger.service.YmlMerger;
import com.tatzan.config.merger.util.JsonUtils;
import com.tatzan.config.merger.util.MapUtils;
import org.atteo.xmlcombiner.XmlCombiner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ConfigMergerImpl implements ConfigMerger {

    private final JSONParser parser = new JSONParser();

    @Override
    public List<File> merge(List<ConfigMetadata> configMetadataList) throws IOException, ParserConfigurationException, TransformerException, SAXException, ParseException {

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

        return mergeConfigs(filesMap);
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


    private List<File> mergeConfigs(Map<String, List<ConfigMetadata>> configMaps) throws IOException, ParserConfigurationException, TransformerException, SAXException, ParseException {
        Iterator iterator = configMaps.entrySet().iterator();
        List<File> filesList = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            filesList.add(mergeConfig((String) pair.getKey(), (List<ConfigMetadata>) pair.getValue()));
            iterator.remove();
        }
        return filesList;
    }

    private File mergeConfig(String outputFileName, List<ConfigMetadata> configMetadataList) throws IOException, ParserConfigurationException, TransformerException, SAXException, ParseException {
        File outputFile = new File(outputFileName);
        List<Object> elements = new ArrayList<>();

        for (ConfigMetadata configMetadata : configMetadataList) {
            if (configMetadata.getInputFilePath() != null) {
                elements.add(new File(configMetadata.getInputFilePath()));
            } else {
                elements.add(configMetadata.getInputString());
            }
        }

        ConfigMetadata firstElement = configMetadataList.get(0);

        if (firstElement.getFileType() == FileType.XML) {
            outputFile = mergeXmlStrings(elements, outputFileName);
        }
        if (firstElement.getFileType() == FileType.JSON) {
            outputFile = mergeJsonStrings(elements, outputFileName);
        }
        if (firstElement.getFileType() == FileType.YAML) {
            outputFile = mergeYamlStrings(elements, outputFileName);
        }

        return outputFile;
    }

    private File mergeYamlFiles(List<File> files, String outputFileName) throws IOException {
        Path outputFile = Paths.get(outputFileName);
        YmlMerger ymlMerger = new YmlMergerImpl();
        List<Path> filesPaths = new ArrayList<>();
        for (File file : files) {
            filesPaths.add(file.toPath());
        }
        String mergedYaml = ymlMerger.mergeToString(filesPaths);
        try (PrintWriter out = new PrintWriter(outputFile.toFile())) {
            out.println(mergedYaml);
        }
        return outputFile.toFile();
    }

    private File mergeYamlStrings(List<Object> elements, String outputFileName) throws IOException {
        Path outputFile = Paths.get(outputFileName);
        YmlMerger ymlMerger = new YmlMergerImpl();
        List<String> yamlStrings = new ArrayList<>();
        for (Object yaml : elements) {
            if (yaml instanceof File) {
                String content = Files.readString(((File) yaml).toPath(), StandardCharsets.UTF_8);
                yamlStrings.add(content);
            } else {
                yamlStrings.add((String) yaml);
            }
        }
        Map<String, Object> mergedYaml = ymlMerger.mergeYamlStrings(yamlStrings);
        String mergedYamlString = ymlMerger.exportToString(mergedYaml);
        try (PrintWriter out = new PrintWriter(outputFile.toFile())) {
            out.println(mergedYamlString);
        }
        return outputFile.toFile();
    }

    private File mergeJsonFiles(List<File> rootFiles, String outputFileName) throws IOException, ParseException {
        Path outputFile = Paths.get(outputFileName);
        JSONObject jsonObject = new JSONObject();

        for (File file : rootFiles) {
            JSONObject jsonObjectFromFile = (JSONObject) parser.parse(
                    new FileReader(file));//path to the JSON file.
            jsonObject = JsonUtils.deepMerge(jsonObjectFromFile, jsonObject);
        }
        try (FileWriter fileWriter = new FileWriter(outputFile.toFile())) {
            fileWriter.write(jsonObject.toString());
        }
        return outputFile.toFile();
    }

    private File mergeJsonStrings(List<Object> elements, String outputFileName) throws IOException, ParseException {
        Path outputFile = Paths.get(outputFileName);
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
        try (FileWriter fileWriter = new FileWriter(outputFile.toFile())) {
            fileWriter.write(jsonObject.toString());
        }
        return outputFile.toFile();
    }

    private File mergeXmlFiles(List<File> rootFiles, String outputFileName) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        Path outputFile = Paths.get(outputFileName);
        XmlCombiner combiner = new XmlCombiner();
        for (File file : rootFiles) {
            combiner.combine(file.toPath());
        }
        combiner.buildDocument(outputFile);
        return new File(outputFileName);
    }

    private File mergeXmlStrings(List<Object> elements, String outputFileName) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        Path outputFile = Paths.get(outputFileName);
        XmlCombiner combiner = new XmlCombiner();
        for (Object xml : elements) {

            if (xml instanceof String) {
                File tempFile = File.createTempFile("prefix-", "-suffix");
                try (FileWriter fileWriter = new FileWriter(tempFile)) {
                    fileWriter.write((String) xml);
                }
                combiner.combine(tempFile.toPath());
                tempFile.deleteOnExit();
            } else {
                Path path = ((File) xml).toPath();
                combiner.combine(path);
            }
        }
        combiner.buildDocument(outputFile);
        return new File(outputFileName);
    }

}
