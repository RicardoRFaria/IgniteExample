package com.example.ignite.IgniteExample.taskrunner;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.compute.ComputeJobAdapter;

import javax.cache.Cache;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Task that count letters contained in cache and put on Map
 * <br>
 * <i><b>Important:</b> This task will be serialized and send across Ignite Nodes, don't put Spring components here or classes that can't be serialized.</i>
 */
public class CountLetterComputeTask extends ComputeJobAdapter {

    private final IgniteCache<String, String> cache;
    private Map<Character, AtomicLong> mapCount;

    /**
     * Default constructor
     *
     * @param cache Cache that contains the lines to count letters
     */
    public CountLetterComputeTask(IgniteCache<String, String> cache) {
        this.cache = cache;
        this.mapCount = new HashMap<>();
    }

    @Override
    public Map<Character, AtomicLong> execute() {
        ScanQuery<String, String> qry = new ScanQuery<>((key, value) -> true);
        qry.setLocal(true);

        try (QueryCursor<Cache.Entry<String, String>> cursor = cache.query(qry)) {

            Stream<Cache.Entry<String, String>> stream = StreamSupport.stream(cursor.spliterator(), false);

            stream.forEach(this::countLetters);

            return mapCount;
        }
    }

    private void countLetters(Cache.Entry<String, String> entry) {
        String value = entry.getValue();
        for (char letter : value.toCharArray()) {
            Character letterUpperCase = Character.toUpperCase(letter);
            mapCount.putIfAbsent(letterUpperCase, new AtomicLong());
            mapCount.get(letterUpperCase).incrementAndGet();
        }
    }

}
