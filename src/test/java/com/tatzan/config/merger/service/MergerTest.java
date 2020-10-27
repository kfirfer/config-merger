package com.tatzan.config.merger.service;

import com.tatzan.config.merger.model.ConfigMetadata;
import com.tatzan.config.merger.model.FileType;
import com.tatzan.config.merger.service.impl.MergerImpl;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergerTest {

    private static final Merger merger = new MergerImpl();

    @Test
    public void testMergerXml() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputFile("tests/input/files-1-1.xml");
        configMetadata.setOutputFile("tests/output/files-1-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFile("tests/input/files-1-2.xml");
        configMetadata.setOutputFile("tests/output/files-1-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFile("tests/input/files-2-1.xml");
        configMetadata.setOutputFile("tests/output/files-2-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);


        List<File> files = merger.merge(filesList);
        System.out.println(files);
    }

    @Test
    public void testMergerJson() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputFile("tests/input/files-3-1.json");
        configMetadata.setOutputFile("tests/output/files-3-output.json");
        configMetadata.setFileType(FileType.JSON);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFile("tests/input/files-3-2.json");
        configMetadata.setOutputFile("tests/output/files-3-output.json");
        configMetadata.setFileType(FileType.JSON);
        filesList.add(configMetadata);

        List<File> files = merger.merge(filesList);
        System.out.println(files);
    }

    @Test
    public void testMergerYaml() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputFile("tests/input/files-4-1.yaml");
        configMetadata.setOutputFile("tests/output/files-4-output.yaml");
        configMetadata.setFileType(FileType.YAML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFile("tests/input/files-4-2.yaml");
        configMetadata.setOutputFile("tests/output/files-4-output.yaml");
        configMetadata.setFileType(FileType.YAML);
        filesList.add(configMetadata);

        List<File> files = merger.merge(filesList);
        System.out.println(files);
    }

    @Test
    public void testMergeMaps() throws IOException, JAXBException {
        List<Map<String, Object>> mapList = new ArrayList<>();

        List<String> list1 = new ArrayList<>();
        list1.add("aaa");
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("arr", list1);
        mapList.add(map1);

        List<String> list2 = new ArrayList<>();
        list2.add("bbb");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("key2", "value2");
        map2.put("arr", list2);

        mapList.add(map2);


        Map<String, Object> outputMap = merger.mergeMaps(mapList);

        merger.mapToJson(outputMap, "tests/output/files-10-output.json");
        merger.mapToYaml(outputMap, "tests/output/files-10-output.yaml");
        merger.mapToXml(outputMap, "tests/output/files-10-output.xml", "root");

    }

}
