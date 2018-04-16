package com.example.ignite.IgniteExample.controller;

import java.util.Map;

public class CountResult {

    private final long totalTime;
    private final Map<Character, Long> result;

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
