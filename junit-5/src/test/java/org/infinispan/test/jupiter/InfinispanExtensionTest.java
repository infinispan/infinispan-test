package org.infinispan.test.jupiter;

import static org.infinispan.test.jupiter.InfinispanServerExtension.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class InfinispanExtensionTest {

    @RegisterExtension
    InfinispanServerExtension server = builder().host("localhost").port(11333).withCaches("initCache").startBeforerEach(true).stopAfterEach(true).build();

    @Test
    public void getPut() {
        RemoteCacheManager remoteCacheManager = server.hotRodClient();
        RemoteCache<String, String> defaultCache = remoteCacheManager.getCache();
        defaultCache.put("jupiter", "works!");

        assertEquals("works!", defaultCache.get("jupiter"));
    }

    @Test
    public void initCache() {
        RemoteCacheManager remoteCacheManager = server.hotRodClient();
        RemoteCache<String, String> initCache = remoteCacheManager.getCache("initCache");
        assertNotNull(initCache);
    }

    @Test
    public void administration() {
        RemoteCacheManager remoteCacheManager = server.hotRodClient();
        RemoteCache<Object, Object> test = remoteCacheManager.administration().getOrCreateCache("test", "default");
        assertNotNull(test);
    }

    @Test
    public void cache_config() {
        RemoteCacheManager remoteCacheManager = server.hotRodClient();
        RemoteCache<Object, Object> test = remoteCacheManager.administration().getOrCreateCache("test", "default");
        assertNotNull(test);
    }
}
