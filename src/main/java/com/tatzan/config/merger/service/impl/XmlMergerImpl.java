package com.tatzan.config.merger.service.impl;

import com.tatzan.config.merger.exception.UnsupportedObjectException;
import com.tatzan.config.merger.service.XmlMerger;
import org.atteo.xmlcombiner.XmlCombiner;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class XmlMergerImpl implements XmlMerger {


    @Override
    public String mergeXmlStrings(List<String> elements) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        return mergeXml(elements);
    }

    @Override
    public String mergeXmlFiles(List<File> elements) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        return mergeXml(elements);
    }

    @Override
    public String mergeXml(Object elements) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        List<Object> elementList = (List<Object>) elements;
        File outputFileTemp = File.createTempFile("prefix-", "-suffix");
        XmlCombiner combiner = new XmlCombiner();
        combine(elementList, combiner);
        combiner.buildDocument(outputFileTemp.toPath());
        String content = Files.readString(outputFileTemp.toPath(), StandardCharsets.UTF_8);
        outputFileTemp.deleteOnExit();
        return content;
    }

    private void combine(List<Object> elements, XmlCombiner combiner) throws IOException, SAXException {
        for (Object xml : elements) {
            if (xml instanceof String) {
                File tempFile = File.createTempFile("prefix-", "-suffix");
                try (FileWriter fileWriter = new FileWriter(tempFile)) {
                    fileWriter.write((String) xml);
                }
                combiner.combine(tempFile.toPath());
                tempFile.deleteOnExit();
            } else if (xml instanceof File) {
                Path path = ((File) xml).toPath();
                combiner.combine(path);
            } else {
                throw new UnsupportedObjectException("Only File/String objects are supported");
            }
        }
    }

}
