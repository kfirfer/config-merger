### Usage

```
        Merger merger = new MergerImpl();

        List<ConfigMetadata> filesList = new ArrayList<>();
        ConfigMetadata configMetadata = new ConfigMetadata();
        configMetadata.setInputFile("tests/input/files-3-1.json");
        configMetadata.setOutputFile("tests/output/files-3-output.json");
        configMetadata.setFileType(FileType.JSON);
        filesList.add(configMetadata);
        configMetadata = new ConfigMetadata();
        configMetadata.setInputFile("tests/input/files-3-2.json");
        configMetadata.setOutputFile("tests/output/files-3-output.json");
        configMetadata.setFileType(FileType.JSON);
        filesList.add(configMetadata);

        List<File> files = merger.merge(filesList);

```