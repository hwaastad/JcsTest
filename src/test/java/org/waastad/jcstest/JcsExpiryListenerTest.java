/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.waastad.jcstest;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Helge Waastad <helge.waastad@datametrix.no>
 */
@Slf4j
public class JcsExpiryListenerTest {

   private CachingProvider cachingProvider;
   private CacheManager cacheManager;
   private Cache<String, LocalDateTime> cache;

   private static final String name = "1";
   private static final LocalDateTime value = LocalDateTime.now();
   private final String cacheName = "my-cache";

   public JcsExpiryListenerTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
      MutableCacheEntryListenerConfiguration<String, LocalDateTime> listenerConfig = new MutableCacheEntryListenerConfiguration<>(FactoryBuilder.factoryOf(MyExpiryListener.class),
              null,
              false,
              false);

      cachingProvider = Caching.getCachingProvider();
      cacheManager = cachingProvider.getCacheManager(cachingProvider.getDefaultURI(), Thread.currentThread().getContextClassLoader(), cachingProvider.getDefaultProperties());
      cacheManager.createCache(
              cacheName,
              new MutableConfiguration<String, LocalDateTime>()
                      .setStoreByValue(false)
                      .setStatisticsEnabled(true)
                      .setManagementEnabled(true)
                      .addCacheEntryListenerConfiguration(listenerConfig)
                      .setTypes(String.class, LocalDateTime.class)
                      .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.MILLISECONDS, 10))));
      cache = cacheManager.getCache(cacheName, String.class, LocalDateTime.class);

   }

   @After
   public void tearDown() {
      cache.close();
      cacheManager.close();
      cachingProvider.close();
   }

   @Test
   public void testOne() throws Exception {
      cache = cacheManager.getCache(cacheName, String.class, LocalDateTime.class);
      cache.put(name, value);
      cache.get(name);
      Thread.sleep(20);
      assertFalse(cache.containsKey(name));
      assertEquals(1,MyExpiryListener.counter.get());
      cache.put(name, value);
      cache.get(name);
      Thread.sleep(20);
      assertFalse(cache.containsKey(name));
      assertEquals(2,MyExpiryListener.counter.get());
   }

}
