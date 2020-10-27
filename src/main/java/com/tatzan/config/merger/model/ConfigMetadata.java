package com.tatzan.config.merger.model;

import lombok.Data;

@Data
public class ConfigMetadata {

    private String inputFilePath;

    private String inputString;

    private String outputFile;

    private FileType fileType;

}
