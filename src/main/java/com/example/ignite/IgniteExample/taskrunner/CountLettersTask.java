package com.example.ignite.IgniteExample.taskrunner;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CountLettersTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountLettersTask.class);

    private final Ignite ignite;
    private final IgniteCache<String, String> cache;

    public CountLettersTask(Ignite ignite, IgniteCache<String, String> cache) {
        this.ignite = ignite;
        this.cache = cache;
    }

    public Map<Character, Long> countLetters() {
        LOGGER.info("Starting 'Letter Count' recalculation for all nodes.");
        long start = System.currentTimeMillis();

        IgniteCompute compute = ignite.compute().withNoFailover();
        Map<Character, Long> result = compute.execute(new CountLetterComputeTaskAdapter(cache), null);

        LOGGER.info(String.format("'Letter Count' processing time: %d ms", System.currentTimeMillis() - start));
        return result;
    }
}
