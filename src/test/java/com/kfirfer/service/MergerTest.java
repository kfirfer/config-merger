package com.kfirfer.service;

import com.kfirfer.model.ConfigMetadata;
import com.kfirfer.model.FileType;
import com.kfirfer.service.impl.MergerImpl;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergerTest {

    private static Merger merger = new MergerImpl();

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

}
