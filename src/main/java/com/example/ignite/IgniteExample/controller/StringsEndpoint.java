package com.example.ignite.IgniteExample.controller;

import com.example.ignite.IgniteExample.service.StringsCacheBootstrap;
import com.example.ignite.IgniteExample.taskrunner.CountLetterComputeTask;
import com.example.ignite.IgniteExample.taskrunner.CountLettersTask;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Controller that exposes the endpoints to perform tasks
 */
@Controller
public class StringsEndpoint {

    private final StringsCacheBootstrap cacheBootstrap;
    private final CountLettersTask task;
    private final IgniteCache<String, String> cache;

    /**
     * default constructor
     *
     * @param cacheBootstrap
     * @param task
     * @param cache
     */
    @Autowired
    public StringsEndpoint(StringsCacheBootstrap cacheBootstrap, CountLettersTask task,
                           IgniteCache<String, String> cache) {
        this.cacheBootstrap = cacheBootstrap;
        this.task = task;
        this.cache = cache;
    }

    /**
     * Load files to cache
     *
     * @return
     */
    @GetMapping("/bootstrap")
    @ResponseBody
    public String bootstrap() {
        Long startTime = System.currentTimeMillis();
        cacheBootstrap.bootstrap();
        Long totalTime = System.currentTimeMillis() - startTime;
        return "Bootstrap finished after " + totalTime + " ms.";
    }

    /**
     * Perform count letters across ignite nodes
     *
     * @return the object with count and total time elapsed
     */
    @GetMapping("/count")
    @ResponseBody
    public CountResult countWords() {
        Long startTime = System.currentTimeMillis();
        Map<Character, Long> result = task.countLetters();
        Long totalTime = System.currentTimeMillis() - startTime;
        return new CountResult(totalTime, result);
    }

    /**
     * Perform count letters without send to ignite
     *
     * @return the object with count and total time elapsed
     */
    @GetMapping("/countWithoutIgnite")
    @ResponseBody
    public CountResult countWithoutIgnite() {
        Long startTime = System.currentTimeMillis();
        Map<Character, AtomicLong> result = new CountLetterComputeTask(cache).execute();
        Long totalTime = System.currentTimeMillis() - startTime;
        Map<Character, Long> resultParse = new HashMap<>();
        for (Map.Entry<Character, AtomicLong> entry : result.entrySet()) {
            resultParse.put(entry.getKey(), entry.getValue().get());
        }
        return new CountResult(totalTime, resultParse);
    }

}
