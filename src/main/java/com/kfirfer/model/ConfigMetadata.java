package com.kfirfer.model;

import lombok.Data;

@Data
public class ConfigMetadata {

    private String outputFile;

    private String inputFile;

    private FileType fileType;

}
