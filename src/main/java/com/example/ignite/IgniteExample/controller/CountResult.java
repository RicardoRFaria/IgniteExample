package com.example.ignite.IgniteExample.controller;

import java.util.Map;

/**
 * Class that represents the result of a count
 */
public class CountResult {

    private final long totalTime;
    private final Map<Character, Long> result;

    /**
     * Create new result to return to users
     *
     * @param totalTime total time to execute task
     * @param result    map where key is the letter and value is the count
     */
    public CountResult(long totalTime, Map<Character, Long> result) {
        this.totalTime = totalTime;
        this.result = result;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public Map<Character, Long> getResult() {
        return result;
    }
}
