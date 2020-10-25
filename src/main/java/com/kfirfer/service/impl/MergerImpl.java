package com.kfirfer.service.impl;

import com.kfirfer.exception.DuplicateFileTypeException;
import com.kfirfer.model.ConfigMetadata;
import com.kfirfer.model.FileType;
import com.kfirfer.service.Merger;
import com.kfirfer.util.JsonUtils;
import org.atteo.xmlcombiner.XmlCombiner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

        return outputFile;
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
