package com.kfirfer.service.impl;

import com.kfirfer.exception.DuplicateFileTypeException;
import com.kfirfer.model.ConfigMetadata;
import com.kfirfer.model.FileType;
import com.kfirfer.service.Merger;
import org.atteo.xmlcombiner.XmlCombiner;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MergerImpl implements Merger {

    @Override
    public List<File> merge(List<ConfigMetadata> configMetadataList) throws IOException, ParserConfigurationException, TransformerException, SAXException {

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

    private List<File> mergeFiles(Map<String, List<ConfigMetadata>> filesMap) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        Iterator it = filesMap.entrySet().iterator();
        List<File> filesList = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            filesList.add(mergeFile((String) pair.getKey(), (List<ConfigMetadata>) pair.getValue()));
            it.remove();
        }
        return filesList;

    }

    private File mergeFile(String outputFileName, List<ConfigMetadata> configMetadataFiles) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        File outputFile = new File(outputFileName);
        List<File> files = new ArrayList<>();

        for (ConfigMetadata configMetadata : configMetadataFiles) {
            files.add(new File(configMetadata.getInputFile()));
        }

        if (configMetadataFiles.get(0).getFileType() == FileType.XML) {
            outputFile = mergerXml(files, outputFileName);
        }
        return outputFile;
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
