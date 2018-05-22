package org.waastad.jcstest;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MyExpiryListener implements  CacheEntryExpiredListener<String, LocalDateTime> {

   public static AtomicLong counter = new AtomicLong(0);

   @Override
   public void onExpired(Iterable<CacheEntryEvent<? extends String, ? extends LocalDateTime>> iterable) throws CacheEntryListenerException {
      log.info("{} entries has expired, counter: {}", iterable.spliterator().getExactSizeIfKnown(),counter.get());
      counter.incrementAndGet();
   }
}
