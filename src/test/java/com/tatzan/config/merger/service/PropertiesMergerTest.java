package com.tatzan.config.merger.service;

import com.tatzan.config.merger.model.ConfigMetadata;
import com.tatzan.config.merger.model.ConfigResult;
import com.tatzan.config.merger.model.FileType;
import com.tatzan.config.merger.service.impl.ConfigMergerImpl;
import com.tatzan.config.merger.service.impl.PropertiesMergerImpl;
import com.tatzan.config.merger.util.PropertiesUtils;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesMergerTest {

    private static final ConfigMerger merger = new ConfigMergerImpl();
    PropertiesMerger propertiesMerger = new PropertiesMergerImpl();

    @Test
    public void testPropertiesMerger() throws IOException, TransformerException, SAXException, ParserConfigurationException {
        String content1 = Files.readString(new File("tests/input/files-5-1.properties").toPath(), StandardCharsets.UTF_8);
        String content2 = Files.readString(new File("tests/input/files-5-2.properties").toPath(), StandardCharsets.UTF_8);
        List<Properties> propertiesList = new ArrayList<>();
        propertiesList.add(PropertiesUtils.parsePropertiesString(content1));
        propertiesList.add(PropertiesUtils.parsePropertiesString(content2));
        String properties = propertiesMerger.merge(propertiesList);
        System.out.println(properties);
    }


    @Test
    public void testMerger() throws IOException, TransformerException, SAXException, ParserConfigurationException, XMLStreamException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-5-1.properties");
        configMetadata.setOutputFile("tests/output/files-5-output.properties");
        configMetadata.setFileType(FileType.PROPERTIES);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-5-2.properties");
        configMetadata.setOutputFile("tests/output/files-5-output.properties");
        configMetadata.setFileType(FileType.PROPERTIES);
        filesList.add(configMetadata);

        List<ConfigResult> configResultList = merger.merge(filesList, true);

        Assert.assertNotNull(configResultList);

        Assert.assertTrue(MergerTest.compareFilesContent(new File("tests/output/files-5-output.properties"), new File("tests/expected/files-5-output.properties")));
    }

    @Test
    public void testMergerString() throws SAXException, ParseException, TransformerException, IOException, XMLStreamException, ParserConfigurationException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputString("some.test.1=test1value");
        configMetadata.setOutputFile("tests/output/files-5-output.properties");
        configMetadata.setFileType(FileType.PROPERTIES);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputString("some.test.2=test 2 value");
        configMetadata.setOutputFile("tests/output/files-5-output.properties");
        configMetadata.setFileType(FileType.PROPERTIES);
        filesList.add(configMetadata);

        List<ConfigResult> configResultList = merger.merge(filesList, true);
        System.out.println(configResultList);
    }


}
