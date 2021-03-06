package com.example.ignite.IgniteExample.service;

import org.apache.ignite.IgniteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that fill ignite cache with the data to be processed
 */
@Service
public class StringsCacheBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringsCacheBootstrap.class);

    private final IgniteCache<String, String> cache;

    /**
     * Default constructor
     *
     * @param cache
     */
    @Autowired
    public StringsCacheBootstrap(IgniteCache<String, String> cache) {
        this.cache = cache;
    }

    private List<String> getStrings() {
        try {
            List<File> files = getAllFiles();
            List<String> lines = new ArrayList<>();
            for (File file : files) {
                Path path = file.toPath();
                List<String> fileLines = Files.readAllLines(path);
                lines.addAll(fileLines);
            }
            return lines;
        } catch (IOException e) {
            LOGGER.error("Failed to read file", e);
            throw new RuntimeException(e);
        }
    }

    private List<File> getAllFiles() {
        List<File> textFiles = new ArrayList<>();

        for (File file : getResourceFolderFiles("texts")) {
            if (file.isFile()) {
                textFiles.add(file);
            }
        }
        return textFiles;
    }

    private File[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();
    }

    /**
     * Perform cache bootstrap reading files under /texts path
     */
    public void bootstrap() {
        for (String line : getStrings()) {
            cache.put(line, line);
        }
    }

}
