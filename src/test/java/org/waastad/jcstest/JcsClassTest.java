/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.waastad.jcstest;

import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Helge Waastad <helge.waastad@datametrix.no>
 */
public class JcsClassTest {

    private CachingProvider cachingProvider;
    private CacheManager cacheManager;
    private Cache<Integer, Integer> cache;

    private static final Integer name = 1;
    private static final Integer value = 2;
    private final String cacheName = "my-cache";

    public JcsClassTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        cachingProvider = Caching.getCachingProvider();
        cacheManager = cachingProvider.getCacheManager(cachingProvider.getDefaultURI(), Thread.currentThread().getContextClassLoader(), cachingProvider.getDefaultProperties());
        cacheManager.createCache(
                cacheName,
                new MutableConfiguration<Integer, Integer>()
                .setStoreByValue(false)
                .setStatisticsEnabled(true)
                .setManagementEnabled(true)
                .setTypes(Integer.class, Integer.class)
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.MILLISECONDS, 10))));
        cache = cacheManager.getCache(cacheName, Integer.class, Integer.class);

    }

    @After
    public void tearDown() {
        cache.close();
        cacheManager.close();
        cachingProvider.close();
    }

    @Test
    public void testOne() throws Exception {
        cache = cacheManager.getCache(cacheName, Integer.class, Integer.class);
        cache.put(name, value);
        cache.get(name);
        Thread.sleep(20);
        assertFalse(cache.containsKey(name));
    }

    @Test
    public void testTwo() throws Exception {
        cache = cacheManager.getCache(cacheName, Integer.class, Integer.class);
        cache.put(name, value);
        cache.get(name);
        Thread.sleep(8);
        assertTrue(cache.containsKey(name));
    }

    @Test
    public void testThree() throws Exception {
        cache.put(name, value);
        cache.get(name);
        Thread.sleep(5);
        cache.get(name);
        Thread.sleep(5);
        cache.get(name);
        Thread.sleep(5);
        cache.get(name);
        Thread.sleep(5);
        cache.get(name);
        assertFalse(cache.containsKey(name));
    }

}
