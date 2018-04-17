package com.example.ignite.IgniteExample.taskrunner;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CountLetterComputeTaskAdapter extends ComputeTaskAdapter<String, Map<Character, Long>> {

    private final IgniteCache<String, String> cache;

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

    @Override
    public Map<Character, Long> reduce(List<ComputeJobResult> jobResults) throws IgniteException {
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
}
