package com.tatzan.config.merger.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.tatzan.config.merger.service.ConfigSerialization;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Map;

public class ConfigSerializationImpl implements ConfigSerialization {


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

    @Override
    public Map<String, Object> jsonFileToMap(File inputFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(
                inputFile.getAbsolutePath()), new TypeReference<>() {
        });
    }

    @Override
    public Map<String, Object> yamlFileToMap(File inputFile) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(inputFile)) {
            return yaml.loadAs(in, Map.class);
        }
    }

    @Override
    public Object xmlFileToObject(File inputFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(inputFile);
    }

}
