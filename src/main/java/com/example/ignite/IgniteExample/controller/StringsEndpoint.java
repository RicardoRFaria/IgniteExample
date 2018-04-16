package com.example.ignite.IgniteExample.controller;

import com.example.ignite.IgniteExample.service.StringsCacheBootstrap;
import com.example.ignite.IgniteExample.taskrunner.CountLettersTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class StringsEndpoint {

    private StringsCacheBootstrap cacheBootstrap;
    private CountLettersTask task;

    @Autowired
    public StringsEndpoint(StringsCacheBootstrap cacheBootstrap, CountLettersTask task) {
        this.cacheBootstrap = cacheBootstrap;
        this.task = task;
    }

    @GetMapping("/bootstrap")
    @ResponseBody
    public String bootstrap() {
        Long startTime = System.currentTimeMillis();
        cacheBootstrap.boostrap();
        Long totalTime = System.currentTimeMillis() - startTime;
        return "Bootstrap finished after " + totalTime + " ms.";
    }

    @GetMapping("/count")
    @ResponseBody
    public CountResult countWords() {
        Long startTime = System.currentTimeMillis();
        Map<Character, Long> result = task.countLetters();
        Long totalTime = System.currentTimeMillis() - startTime;
        return new CountResult(totalTime, result);
    }

}
