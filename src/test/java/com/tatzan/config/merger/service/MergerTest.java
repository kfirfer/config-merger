package com.tatzan.config.merger.service;

import com.tatzan.config.merger.model.ConfigMetadata;
import com.tatzan.config.merger.model.ConfigResult;
import com.tatzan.config.merger.model.FileType;
import com.tatzan.config.merger.service.impl.ConfigMergerImpl;
import com.tatzan.config.merger.service.impl.ConfigSerializationImpl;
import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
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

    private static final ConfigMerger merger = new ConfigMergerImpl();
    private static final ConfigSerialization configSerialization = new ConfigSerializationImpl();

    public static boolean compareFilesContent(File file1, File file2) throws IOException {
        return FileUtils.contentEquals(file1, file2);
    }

    @Test
    public void testMergerXml() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-1-1.xml");
        configMetadata.setOutputFile("tests/output/files-1-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-1-2.xml");
        configMetadata.setOutputFile("tests/output/files-1-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-2-1.xml");
        configMetadata.setOutputFile("tests/output/files-2-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);


        List<ConfigResult> configResultList = merger.merge(filesList, true);

        Assert.assertNotNull(configResultList);
        Assert.assertTrue(compareFilesContent(new File("tests/output/files-1-output.xml"), new File("tests/expected/files-1-output.xml")));
        Assert.assertTrue(compareFilesContent(new File("tests/output/files-2-output.xml"), new File("tests/expected/files-2-output.xml")));
    }

    @Test
    public void testMergerJson() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-3-1.json");
        configMetadata.setOutputFile("tests/output/files-3-output.json");
        configMetadata.setFileType(FileType.JSON);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-3-2.json");
        configMetadata.setOutputFile("tests/output/files-3-output.json");
        configMetadata.setFileType(FileType.JSON);
        filesList.add(configMetadata);

        List<ConfigResult> configResultList = merger.merge(filesList, true);
        Assert.assertNotNull(configResultList);

        Assert.assertTrue(compareFilesContent(new File("tests/output/files-3-output.json"), new File("tests/expected/files-3-output.json")));

    }

    @Test
    public void testMergerYaml() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-4-1.yaml");
        configMetadata.setOutputFile("tests/output/files-4-output.yaml");
        configMetadata.setFileType(FileType.YAML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-4-2.yaml");
        configMetadata.setOutputFile("tests/output/files-4-output.yaml");
        configMetadata.setFileType(FileType.YAML);
        filesList.add(configMetadata);

        List<ConfigResult> configResultList = merger.merge(filesList, true);

        Assert.assertNotNull(configResultList);

        Assert.assertTrue(compareFilesContent(new File("tests/output/files-4-output.yaml"), new File("tests/expected/files-4-output.yaml")));
    }

    @Test
    public void testMergerYamlString() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputString("key1:\n" +
                "  key2:\n" +
                "    value1: \"value1\"\n" +
                "    array:\n" +
                "      - name: one\n" +
                "        version: 1\n");
        configMetadata.setOutputFile("tests/output/files-4-output.yaml");
        configMetadata.setFileType(FileType.YAML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputString("key1:\n" +
                "  key2:\n" +
                "    value2: \"value2\"\n" +
                "    array:\n" +
                "      - name: two\n" +
                "        version: 2\n" +
                "\n");
        configMetadata.setOutputFile("tests/output/files-4-output.yaml");
        configMetadata.setFileType(FileType.YAML);
        filesList.add(configMetadata);

        List<ConfigResult> configResultList = merger.merge(filesList, true);
        System.out.println(configResultList);
    }

    @Test
    public void testMergerXmlString() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <groupId>com.kfirfer</groupId>\n" +
                "    <artifactId>config-merger</artifactId>\n" +
                "    <version>0.0.1-SNAPSHOT</version>\n" +
                "\n" +
                "</project>");
        configMetadata.setOutputFile("tests/output/files-1-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "    <dependencies>\n" +
                "\n" +
                "        <dependency>\n" +
                "            <groupId>commons-codec</groupId>\n" +
                "            <artifactId>commons-codec</artifactId>\n" +
                "            <version>1.14</version>\n" +
                "        </dependency>\n" +
                "\n" +
                "        <dependency>\n" +
                "            <groupId>junit</groupId>\n" +
                "            <artifactId>junit</artifactId>\n" +
                "            <version>4.12</version>\n" +
                "            <scope>test</scope>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "\n" +
                "</project>");
        configMetadata.setOutputFile("tests/output/files-1-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <groupId>com.kfirfer</groupId>\n" +
                "    <artifactId>config-merger</artifactId>\n" +
                "    <version>0.0.1-SNAPSHOT</version>\n" +
                "\n" +
                "    <properties>\n" +
                "        <java.version>11</java.version>\n" +
                "        <jacoco.version>0.8.5</jacoco.version>\n" +
                "    </properties>\n" +
                "\n" +
                "    <dependencies>\n" +
                "\n" +
                "        <dependency>\n" +
                "            <groupId>commons-codec</groupId>\n" +
                "            <artifactId>commons-codec</artifactId>\n" +
                "            <version>1.14</version>\n" +
                "        </dependency>\n" +
                "\n" +
                "        <dependency>\n" +
                "            <groupId>junit</groupId>\n" +
                "            <artifactId>junit</artifactId>\n" +
                "            <version>4.12</version>\n" +
                "            <scope>test</scope>\n" +
                "        </dependency>\n" +
                "\n" +
                "        <dependency>\n" +
                "            <groupId>org.atteo</groupId>\n" +
                "            <artifactId>xml-combiner</artifactId>\n" +
                "            <version>2.2</version>\n" +
                "        </dependency>\n" +
                "\n" +
                "        <dependency>\n" +
                "            <groupId>org.projectlombok</groupId>\n" +
                "            <artifactId>lombok</artifactId>\n" +
                "            <version>1.18.12</version>\n" +
                "            <scope>provided</scope>\n" +
                "        </dependency>\n" +
                "\n" +
                "    </dependencies>\n" +
                "    <build>\n" +
                "        <finalName>app</finalName>\n" +
                "        <plugins>\n" +
                "            <plugin>\n" +
                "                <groupId>org.apache.maven.plugins</groupId>\n" +
                "                <artifactId>maven-compiler-plugin</artifactId>\n" +
                "                <version>3.8.1</version>\n" +
                "                <configuration>\n" +
                "                    <source>${java.version}</source>\n" +
                "                    <target>${java.version}</target>\n" +
                "                    <encoding>UTF-8</encoding>\n" +
                "                </configuration>\n" +
                "            </plugin>\n" +
                "            <plugin>\n" +
                "                <groupId>org.jacoco</groupId>\n" +
                "                <artifactId>jacoco-maven-plugin</artifactId>\n" +
                "                <version>${jacoco.version}</version>\n" +
                "                <executions>\n" +
                "                    <execution>\n" +
                "                        <goals>\n" +
                "                            <goal>prepare-agent</goal>\n" +
                "                        </goals>\n" +
                "                    </execution>\n" +
                "                    <execution>\n" +
                "                        <id>report</id>\n" +
                "                        <phase>prepare-package</phase>\n" +
                "                        <goals>\n" +
                "                            <goal>report</goal>\n" +
                "                        </goals>\n" +
                "                    </execution>\n" +
                "                </executions>\n" +
                "            </plugin>\n" +
                "        </plugins>\n" +
                "\n" +
                "    </build>\n" +
                "\n" +
                "\n" +
                "</project>\n");
        configMetadata.setOutputFile("tests/output/files-2-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);


        List<ConfigResult> configResultList = merger.merge(filesList, true);
        System.out.println(configResultList);
    }

    @Test
    public void testMergerJsonStrings() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputString("{\n" +
                "  \"property1\": \"value1\",\n" +
                "  \"deep_property\": {\n" +
                "    \"array1\": [\n" +
                "      \"arr1\",\n" +
                "      \"arr2\"\n" +
                "    ]\n" +
                "  }\n" +
                "}");
        configMetadata.setOutputFile("tests/output/files-3-output.json");
        configMetadata.setFileType(FileType.JSON);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputString("{\n" +
                "  \"property2\": \"value2\",\n" +
                "  \"deep_property\": {\n" +
                "    \"array1\": [\n" +
                "      \"arr3\",\n" +
                "      \"arr4\"\n" +
                "    ]\n" +
                "  }\n" +
                "}");
        configMetadata.setOutputFile("tests/output/files-3-output.json");
        configMetadata.setFileType(FileType.JSON);
        filesList.add(configMetadata);

        List<ConfigResult> configResultList = merger.merge(filesList, true);
        System.out.println(configResultList);
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

        configSerialization.mapToJson(outputMap, "tests/output/strings-1-output.json");
        configSerialization.mapToYaml(outputMap, "tests/output/strings-1-output.yaml");
        configSerialization.mapToXml(outputMap, "tests/output/strings-1-output.xml", "root");

    }

    @Test
    public void testJsonFileToMap() throws IOException {
        File file = new File("tests/input/files-3-1.json");
        Map<String, Object> outputMap = configSerialization.jsonFileToMap(file);
        System.out.println(outputMap);
    }

    @Test
    public void testYamlFileToMap() throws IOException {
        File file = new File("tests/input/files-4-1.yaml");
        Map<String, Object> outputMap = configSerialization.yamlFileToMap(file);
        System.out.println(outputMap);
    }

    @Test
    public void testXmlFileToObject() throws IOException, JAXBException, TransformerException, ParserConfigurationException, SAXException {
        File file = new File("tests/input/files-1-2.xml");
        Object outputObject = configSerialization.xmlFileToObject(file);
        System.out.println(outputObject);
    }


    @Test
    public void testMergerXmlString2() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputString("<project>\n" +
                "<dependencies>\n" +
                "<dependency>\n" +
                "<groupId>rrrrr</groupId>\n" +
                "</dependency>\n" +
                "</dependencies>\n" +
                "</project>");
        configMetadata.setOutputFile("tests/output/files-1-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputString("<project>\n" +
                "<dependencies>\n" +
                "<dependency>\n" +
                "<groupId>tttttt</groupId>\n" +
                "</dependency>\n" +
                "</dependencies>\n" +
                "</project>");
        configMetadata.setOutputFile("tests/output/files-1-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);


        List<ConfigResult> configResultList = merger.merge(filesList, true);
        System.out.println(configResultList);
    }


    @Test
    public void testMergerXml2() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-6-1.xml");
        configMetadata.setOutputFile("tests/output/files-6-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-6-2.xml");
        configMetadata.setOutputFile("tests/output/files-6-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);

        List<ConfigResult> configResultList = merger.merge(filesList, true);

        Assert.assertNotNull(configResultList);
        Assert.assertTrue(compareFilesContent(new File("tests/output/files-6-output.xml"), new File("tests/expected/files-6-output.xml")));

    }


    @Test
    public void testMergerXml3() throws IOException, XMLStreamException, TransformerException, ParserConfigurationException, SAXException, ParseException {
        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-7-1.xml");
        configMetadata.setOutputFile("tests/output/files-7-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFilePath("tests/input/files-7-2.xml");
        configMetadata.setOutputFile("tests/output/files-7-output.xml");
        configMetadata.setFileType(FileType.XML);
        filesList.add(configMetadata);

        List<ConfigResult> configResultList = merger.merge(filesList, true);

        Assert.assertNotNull(configResultList);
        Assert.assertTrue(compareFilesContent(new File("tests/output/files-7-output.xml"), new File("tests/expected/files-7-output.xml")));

    }

}
