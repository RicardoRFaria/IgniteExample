package com.example.ignite.IgniteExample.taskrunner;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Task adapter that distribute {@link CountLetterComputeTask} to Ignite nodes and do the reduce
 */
public final class CountLetterComputeTaskAdapter extends ComputeTaskAdapter<String, Map<Character, Long>> {

    private static final long ZERO = 0L;

    private final IgniteCache<String, String> cache;

    /**
     * Default constructor
     *
     * @param cache Cache that contains the lines to count letters
     */
    public CountLetterComputeTaskAdapter(IgniteCache<String, String> cache) {
        this.cache = cache;
    }

    @Override
    public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> subgrid, String arg) throws IgniteException {
        Map<ComputeJob, ClusterNode> map = new ConcurrentHashMap<>(subgrid.size());

        for (ClusterNode node : subgrid) {
            map.put(new CountLetterComputeTask(cache), node);
        }
        return map;
    }

    /**
     * Merge maps received from nodes into a new map
     *
     * @param jobResults received from all nodes
     * @return new merged Map
     * @throws IgniteException
     */
    @Override
    public Map<Character, Long> reduce(List<ComputeJobResult> jobResults) throws IgniteException {
        Map<Character, Long> result = new HashMap<>();
        for (ComputeJobResult jobResult : jobResults) {
            Map<Character, AtomicLong> taskResult = jobResult.getData();
            for (Map.Entry<Character, AtomicLong> entryTaskResult : taskResult.entrySet()) {
                Character key = entryTaskResult.getKey();
                result.putIfAbsent(key, ZERO);
                Long oldValue = result.get(key);
                Long value = entryTaskResult.getValue().get();
                result.put(key, oldValue + value);
            }
        }
        return result;
    }
}
