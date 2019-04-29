package org.infinispan.test.jupiter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class InfinispanExtensionTest {

    @RegisterExtension
    static InfinispanServerExtension serverExtension = new InfinispanServerExtension();

    @BeforeAll
    public static void start() {
        serverExtension.start();
    }

    @AfterAll
    public static void stop() {
        serverExtension.stop();
    }

    @Test
    public void infinispanServerExtension() {
        RemoteCacheManager remoteCacheManager = serverExtension.hotRodClient();
        RemoteCache<String, String> defaultCache = remoteCacheManager.getCache();
        defaultCache.put("jupiter", "works!");

        assertEquals("works!", defaultCache.get("jupiter"));
    }
}
