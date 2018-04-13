package com.example.ignite.IgniteExample.config;

import org.apache.ignite.IgniteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class StringsCacheBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringsCacheBootstrap.class);

    private final IgniteCache<String, String> cache;

    @Autowired
    public StringsCacheBootstrap(IgniteCache<String, String> cache) {
        this.cache = cache;
        boostrap();
    }

    private List<String> getStrings() {
        try {
            URI uri = StringsCacheBootstrap.class.getResource("/big.txt").toURI();
            return Files.readAllLines(Paths.get(uri));
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Falha critica ao ler arquivo", e);
            throw new RuntimeException(e);
        }
    }

    public void boostrap() {
        for (String linha : getStrings()) {
            cache.put(linha, linha);
        }
    }

}
