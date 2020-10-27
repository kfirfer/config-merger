package com.tatzan.config.merger.service.impl;

import com.google.gson.Gson;
import com.tatzan.config.merger.exception.DuplicateFileTypeException;
import com.tatzan.config.merger.model.ConfigMetadata;
import com.tatzan.config.merger.model.FileType;
import com.tatzan.config.merger.service.Merger;
import com.tatzan.config.merger.util.JsonUtils;
import com.tatzan.config.merger.util.MapUtils;
import com.tatzan.config.merger.service.YmlMerger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.atteo.xmlcombiner.XmlCombiner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MergerImpl implements Merger {

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
            if (tempTypes.get(configMetadata.getOutputFile()) != configMetadata.getFileType()) {
                throw new DuplicateFileTypeException("Only one type of config file is allowed for a single file");
            }
            List<ConfigMetadata> filesList = filesMap.get(configMetadata.getOutputFile());
            filesList.add(configMetadata);
        }

        return mergeFiles(filesMap);
    }

    @Override
    public Map<String, Object> mergeMaps(List<Map<String, Object>> mapList) {
        Map<String, Object> outputMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            outputMap = MapUtils.deepMerge(outputMap, map);
        }
        return outputMap;
    }

    @Override
    public File mapToJson(Map<String, Object> map, String outputFilePath) throws FileNotFoundException {
        File outputFile = new File(outputFilePath);
        Gson gson = new Gson();
        String json = gson.toJson(map);

        try (PrintWriter out = new PrintWriter(outputFile)) {
            out.println(json);
        }
        return outputFile;
    }

    @Override
    public File mapToYaml(Map<String, Object> map, String outputFilePath) throws FileNotFoundException {
        File outputFile = new File(outputFilePath);
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);
        String output = yaml.dump(map);

        try (PrintWriter out = new PrintWriter(outputFile)) {
            out.println(output);
        }

        return null;
    }

    @Override
    public File mapToXml(Map<String, Object> map, String outputFilePath, String rootElement) throws FileNotFoundException {
        File outputFile = new File(outputFilePath);
        XStream xStream = new XStream(new DomDriver());
        xStream.alias(rootElement, java.util.Map.class);
        String xml = xStream.toXML(map);
        try (PrintWriter out = new PrintWriter(outputFile)) {
            out.println(xml);
        }
        return outputFile;
    }

    private List<File> mergeFiles(Map<String, List<ConfigMetadata>> filesMap) throws IOException, ParserConfigurationException, TransformerException, SAXException, ParseException {
        Iterator it = filesMap.entrySet().iterator();
        List<File> filesList = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            filesList.add(mergeFile((String) pair.getKey(), (List<ConfigMetadata>) pair.getValue()));
            it.remove();
        }
        return filesList;

    }

    private File mergeFile(String outputFileName, List<ConfigMetadata> configMetadataFiles) throws IOException, ParserConfigurationException, TransformerException, SAXException, ParseException {
        File outputFile = new File(outputFileName);
        List<File> files = new ArrayList<>();

        for (ConfigMetadata configMetadata : configMetadataFiles) {
            files.add(new File(configMetadata.getInputFile()));
        }
        ConfigMetadata firstElement = configMetadataFiles.get(0);
        if (firstElement.getFileType() == FileType.XML) {
            outputFile = mergerXml(files, outputFileName);
        }
        if (firstElement.getFileType() == FileType.JSON) {
            outputFile = mergeJson(files, outputFileName);
        }

        if (firstElement.getFileType() == FileType.YAML) {
            outputFile = mergeYaml(files, outputFileName);
        }

        return outputFile;
    }

    private File mergeYaml(List<File> files, String outputFileName) throws IOException {
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

    private File mergeJson(List<File> rootFiles, String outputFileName) throws IOException, ParseException {
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

    private File mergerXml(List<File> rootFiles, String outputFileName) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        Path outputFile = Paths.get(outputFileName);
        XmlCombiner combiner = new XmlCombiner();
        for (File file : rootFiles) {
            combiner.combine(file.toPath());
        }
        combiner.buildDocument(outputFile);
        return new File(outputFileName);
    }

}
