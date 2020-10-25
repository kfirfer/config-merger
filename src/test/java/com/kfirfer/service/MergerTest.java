package com.kfirfer.service;

import com.kfirfer.model.ConfigMetadata;
import com.kfirfer.model.FileType;
import com.kfirfer.service.impl.MergerImpl;
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
    public void testMerger() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException {
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

}
