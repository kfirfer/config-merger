package com.tatzan.config.merger.service.impl;

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
import java.nio.file.Paths;
import java.util.List;

public class XmlMergerImpl implements XmlMerger {

    @Override
    public File mergeXml(List<Object> elements, String outputFileName) throws IOException, SAXException, TransformerException, ParserConfigurationException {
        Path outputFile = Paths.get(outputFileName);
        XmlCombiner combiner = new XmlCombiner();
        combine(elements, combiner);
        combiner.buildDocument(outputFile);
        return new File(outputFileName);

    }

    @Override
    public String mergeXml(List<Object> elements) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        File outputFileTemp = File.createTempFile("prefix-", "-suffix");
        XmlCombiner combiner = new XmlCombiner();

        combine(elements, combiner);
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
            } else {
                Path path = ((File) xml).toPath();
                combiner.combine(path);
            }
        }
    }

}
