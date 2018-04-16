package com.example.ignite.IgniteExample.taskrunner;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTask;
import org.apache.ignite.compute.ComputeTaskAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
        ComputeTask<String, Map<Character, Long>>
                task =
                new ComputeTaskAdapter<String, Map<Character, Long>>() {

                    @Override
                    public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> subgrid, String arg)
                            throws IgniteException {
                        Map<ComputeJob, ClusterNode> map = new ConcurrentHashMap<>(subgrid.size());

                        for (ClusterNode node : subgrid) {
                            map.put(new CountLetterComputeTask(cache), node);
                        }
                        return map;
                    }

                    @Override
                    public Map<Character, Long> reduce(List<ComputeJobResult> jobResults)
                            throws IgniteException {
                        Map<Character, Long> result = new HashMap<>();
                        for (ComputeJobResult jobResult : jobResults) {
                            Map<Character, AtomicLong> taskResult = jobResult.getData();
                            for (Map.Entry<Character, AtomicLong> entryTaskResult : taskResult.entrySet()) {
                                Character key = entryTaskResult.getKey();
                                result.putIfAbsent(key, 0L);
                                Long oldValue = result.get(key);
                                Long value = entryTaskResult.getValue().get();
                                result.put(key, oldValue + value);
                            }
                        }
                        return result;
                    }
                };

        IgniteCompute compute = ignite.compute().withNoFailover();
        Map<Character, Long> result = compute.execute(task, null);

        LOGGER.info(String.format("'Letter Count' processing time: %d ms", System.currentTimeMillis() - start));
        return result;
    }
}
