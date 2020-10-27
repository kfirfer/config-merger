package com.kfirfer.service;

import com.kfirfer.service.impl.YmlMergerImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface YmlMerger {

    String mergeToString(List<Path> filesToMerge) throws IOException;

    YmlMergerImpl setVariablesToReplace(Map<String, String> vars);

    Map<String, Object> mergeYamlFiles(String[] pathsStr) throws IOException;

    Map<String, Object> mergeYamlFiles(List<Path> paths) throws IOException;

}
