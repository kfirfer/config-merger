package com.tatzan.config.merger.service.impl;


import com.github.mustachejava.DefaultMustacheFactory;
import com.tatzan.config.merger.service.YmlMerger;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * (c) Copyright 2013-2015 Jonathan Cobb
 * This code is available under the Apache License, version 2: http://www.apache.org/licenses/LICENSE-2.0.html
 */
public class YmlMergerImpl implements YmlMerger {

    public static final DefaultMustacheFactory DEFAULT_MUSTACHE_FACTORY = new DefaultMustacheFactory();

    private final Yaml snakeYaml;
    private Map<String, Object> variablesToReplace = new HashMap<>();

    public YmlMergerImpl() {
        // See https://github.com/spariev/snakeyaml/blob/master/src/test/java/org/yaml/snakeyaml/DumperOptionsTest.java
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.snakeYaml = new Yaml(dumperOptions);
    }

    private static IllegalArgumentException unknownValueType(String key, Object yamlValue) {
        final String msg = "Cannot mergeYamlFiles element of unknown type: " + key + ": " + yamlValue.getClass().getName();
        return new IllegalArgumentException(msg);
    }

    private static Object addToMergedResult(Map<String, Object> mergedResult, String key, Object yamlValue) {
        return mergedResult.put(key, yamlValue);
    }

    @SuppressWarnings("unchecked")
    private static void mergeLists(Map<String, Object> mergedResult, String key, Object yamlValue) {
        if (!(yamlValue instanceof List && mergedResult.get(key) instanceof List)) {
            throw new IllegalArgumentException("Cannot mergeYamlFiles a list with a non-list: " + key);
        }

        List<Object> originalList = (List<Object>) mergedResult.get(key);
        originalList.addAll((List<Object>) yamlValue);
    }

    public static List<Path> stringsToPaths(String[] pathsStr) {
        Set<Path> paths = new LinkedHashSet<>();
        for (String pathStr : pathsStr) {
            paths.add(Paths.get(pathStr));
        }
        List<Path> pathsList = new ArrayList<>(paths.size());
        pathsList.addAll(paths);
        return pathsList;
    }

    public YmlMergerImpl setVariablesToReplace(Map<String, String> vars) {
        this.variablesToReplace.clear();
        this.variablesToReplace.putAll(vars);
        return this;
    }

    public Map<String, Object> mergeYamlFiles(String[] pathsStr) throws IOException {
        return mergeYamlFiles(stringsToPaths(pathsStr));
    }

    /**
     * Merges the files at given paths to a map representing the resulting YAML structure.
     */
    public Map<String, Object> mergeYamlFiles(List<Path> paths) throws IOException {
        Map<String, Object> mergedResult = new LinkedHashMap<>();
        for (Path yamlFilePath : paths) {
            InputStream in = null;
            try {
                File file = yamlFilePath.toFile();
                if (!file.exists())
                    throw new FileNotFoundException("YAML file to merge not found: " + file.getCanonicalPath());

                // Read the YAML file into a String
                in = new FileInputStream(file);
                final String entireFile = IOUtils.toString(in);

                // Substitute variables. TODO: This should be done by a resolver when parsing.
                int bufferSize = entireFile.length() + 100;
                final StringWriter writer = new StringWriter(bufferSize);
                DEFAULT_MUSTACHE_FACTORY.compile(new StringReader(entireFile), "yaml-mergeYamlFiles-" + System.currentTimeMillis()).execute(writer, variablesToReplace);

                // Parse the YAML.
                String yamlString = writer.toString();
                final Map<String, Object> yamlToMerge = this.snakeYaml.load(yamlString);

                // Merge into results map.
                mergeStructures(mergedResult, yamlToMerge);

            } finally {
                if (in != null) in.close();
            }
        }
        return mergedResult;
    }

    public Map<String, Object> mergeYamlStrings(List<String> yamlStrings) {
        Map<String, Object> mergedResult = new LinkedHashMap<>();
        for (String yamlString : yamlStrings) {
            final Map<String, Object> yamlToMerge = this.snakeYaml.load(yamlString);
            // Merge into results map.
            mergeStructures(mergedResult, yamlToMerge);
        }
        return mergedResult;
    }

    @SuppressWarnings("unchecked")
    private void mergeStructures(Map<String, Object> targetTree, Map<String, Object> sourceTree) {
        if (sourceTree == null) return;

        for (String key : sourceTree.keySet()) {

            Object yamlValue = sourceTree.get(key);
            if (yamlValue == null) {
                addToMergedResult(targetTree, key, yamlValue);
                continue;
            }

            Object existingValue = targetTree.get(key);
            if (existingValue != null) {
                if (yamlValue instanceof Map) {
                    if (existingValue instanceof Map) {
                        mergeStructures((Map<String, Object>) existingValue, (Map<String, Object>) yamlValue);
                    } else if (existingValue instanceof String) {
                        throw new IllegalArgumentException("Cannot mergeYamlFiles complex element into a simple element: " + key);
                    } else {
                        throw unknownValueType(key, yamlValue);
                    }
                } else if (yamlValue instanceof List) {
                    mergeLists(targetTree, key, yamlValue);

                } else if (yamlValue instanceof String
                        || yamlValue instanceof Boolean
                        || yamlValue instanceof Double
                        || yamlValue instanceof Integer) {
                    addToMergedResult(targetTree, key, yamlValue);

                } else {
                    throw unknownValueType(key, yamlValue);
                }

            } else {
                if (yamlValue instanceof Map
                        || yamlValue instanceof List
                        || yamlValue instanceof String
                        || yamlValue instanceof Boolean
                        || yamlValue instanceof Integer
                        || yamlValue instanceof Double) {
                    addToMergedResult(targetTree, key, yamlValue);
                } else {
                    throw unknownValueType(key, yamlValue);
                }
            }
        }
    }

    public String mergeToString(List<Path> filesToMerge) throws IOException {
        Map<String, Object> merged = mergeYamlFiles(filesToMerge);
        return exportToString(merged);
    }

    // Util methods

    public String exportToString(Map<String, Object> merged) {
        return snakeYaml.dump(merged);
    }

    @Override
    public File mergeYaml(List<Object> elements, String outputFileName) throws IOException {
        Path outputFile = Paths.get(outputFileName);
        List<String> yamlStrings = new ArrayList<>();
        for (Object yaml : elements) {
            if (yaml instanceof File) {
                String content = Files.readString(((File) yaml).toPath(), StandardCharsets.UTF_8);
                yamlStrings.add(content);
            } else {
                yamlStrings.add((String) yaml);
            }
        }
        Map<String, Object> mergedYaml = mergeYamlStrings(yamlStrings);
        String mergedYamlString = exportToString(mergedYaml);
        try (PrintWriter out = new PrintWriter(outputFile.toFile())) {
            out.println(mergedYamlString);
        }
        return outputFile.toFile();
    }

    @Override
    public String mergeYaml(List<Object> elements) throws IOException {
        List<String> yamlStrings = new ArrayList<>();
        for (Object yaml : elements) {
            if (yaml instanceof File) {
                String content = Files.readString(((File) yaml).toPath(), StandardCharsets.UTF_8);
                yamlStrings.add(content);
            } else {
                yamlStrings.add((String) yaml);
            }
        }
        Map<String, Object> mergedYaml = mergeYamlStrings(yamlStrings);
        String mergedYamlString = exportToString(mergedYaml);
        return mergedYamlString;
    }

}