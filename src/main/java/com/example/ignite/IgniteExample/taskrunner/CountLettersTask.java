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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CountLettersTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountLettersTask.class);

    private final Ignite ignite;
    private final IgniteCache<String, String> cache;

    public CountLettersTask(Ignite ignite, IgniteCache<String, String> cache) {
        this.ignite = ignite;
        this.cache = cache;
    }

    @Scheduled(initialDelay = 200, fixedRate = 200000)
    public void processMerchantStatus() {
        LOGGER.info("Starting 'Letter Count' recalculation for all nodes.");
        long start = System.currentTimeMillis();
        ComputeTask<String, List<Long>>
                task =
                new ComputeTaskAdapter<String, List<Long>>() {

                    @Override
                    public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> subgrid, String arg)
                            throws IgniteException {
                        Map<ComputeJob, ClusterNode> map = new ConcurrentHashMap<>(subgrid.size());

                        for (ClusterNode node : subgrid) {
                            map.put(new CountLetterComputeTask(cache, 'A'), node);
                        }
                        return map;
                    }

                    @Override
                    public List<Long> reduce(List<ComputeJobResult> jobResults)
                            throws IgniteException {
                        List<Long> reducedResult = new ArrayList<>();
                        for (ComputeJobResult jobResult : jobResults) {
                            reducedResult.add(jobResult.getData());
                        }
                        return reducedResult;
                    }
                };

        IgniteCompute compute = ignite.compute().withNoFailover();
        List<Long> results = compute.execute(task, null);

        LOGGER.info(String.format("'Letter Count' processing time: %d seconds",
                (System.currentTimeMillis() - start) / 1000));
        System.out.println("Total of found letters: " + results.get(0));
    }
}
