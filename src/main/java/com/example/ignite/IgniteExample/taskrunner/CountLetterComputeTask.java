package com.example.ignite.IgniteExample.taskrunner;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.compute.ComputeJobAdapter;

import javax.cache.Cache;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CountLetterComputeTask extends ComputeJobAdapter {

    private final IgniteCache<String, String> cache;
    private Long count = 0L;
    private char lowerCaseLetter;
    private char upperCaseLetter;

    public CountLetterComputeTask(IgniteCache<String, String> cache, char letter) {
        this.cache = cache;
        lowerCaseLetter = Character.toLowerCase(letter);
        upperCaseLetter = Character.toUpperCase(letter);
    }

    @Override
    public Object execute() {
        ScanQuery<String, String> qry = new ScanQuery<>((key, value) -> true);
        qry.setLocal(true);

        try (QueryCursor<Cache.Entry<String, String>> cursor = cache.query(qry)) {

            Stream<Cache.Entry<String, String>> stream = StreamSupport.stream(cursor.spliterator(), false);

            stream.forEach(this::countLetters);

            return count;
        }
    }

    private void countLetters(Cache.Entry<String, String> entry) {
        String value = entry.getValue();
        for (char letter : value.toCharArray()) {
            if (letter == lowerCaseLetter || letter == upperCaseLetter) {
                count ++;
            }
        }
    }

}
