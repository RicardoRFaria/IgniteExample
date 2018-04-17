package com.example.ignite.IgniteExample.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import javax.cache.expiry.EternalExpiryPolicy;

@Configuration
public class IgniteCacheConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(IgniteCacheConfiguration.class);

    private static final String CACHE_MODEL_NAME = "strings-cache-v1";

    public static final int NETWORK_TIMEOUT = 30000;
    public static final int RETRY_COUNT = 10;


    @Bean
    org.apache.ignite.configuration.IgniteConfiguration igniteConfiguration() {
        CacheConfiguration cacheConfiguration = new CacheConfiguration(CACHE_MODEL_NAME);
        cacheConfiguration.setExpiryPolicyFactory(EternalExpiryPolicy.factoryOf());
        cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);

        org.apache.ignite.configuration.IgniteConfiguration
                igniteConfiguration = new org.apache.ignite.configuration.IgniteConfiguration();
        DataStorageConfiguration dataStorage = new DataStorageConfiguration();

        igniteConfiguration.setNetworkTimeout(NETWORK_TIMEOUT);
        igniteConfiguration.setNetworkSendRetryCount(RETRY_COUNT);

        igniteConfiguration.setCacheConfiguration(cacheConfiguration);
        igniteConfiguration.setDataStorageConfiguration(dataStorage);

        igniteConfiguration.setPeerClassLoadingEnabled(true);

        return igniteConfiguration;
    }

    @Bean
    public Ignite getIgnite(org.apache.ignite.configuration.IgniteConfiguration igniteConfiguration) {
        Ignite ignite = Ignition.start(igniteConfiguration);
        ignite.active(true);
        return ignite;
    }

    @Bean("stringsCache")
    public IgniteCache<String, String> getMerchantCache(Ignite ignite) {
        return ignite.getOrCreateCache(CACHE_MODEL_NAME);
    }

    @PreDestroy
    private void shutdownHook() {
        Ignite ignite = Ignition.ignite();
        if (ignite != null) {
            ignite.close();
        }
    }
}
