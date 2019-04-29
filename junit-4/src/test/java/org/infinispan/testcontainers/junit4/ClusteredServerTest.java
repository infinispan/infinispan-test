package org.infinispan.testcontainers.junit4;

import static org.junit.Assert.assertEquals;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tristan Tarrant &lt;tristan@infinispan.org&gt;
 * @since 10.0
 **/
@ServerTestConfiguration
public class ClusteredServerTest {

    @ClassRule
    public static ServerTestRule serverTestRule = new ServerTestRule();

    @Rule
    public ServerTestMethodRule serverTestMethodRule = new ServerTestMethodRule(serverTestRule);

    @Test
    @ServerTestMethodConfiguration
    public void testCluster() {
        RemoteCacheManager client = serverTestRule.hotRodClient();
        RemoteCache<String, String> cache = client.getCache();
        cache.put("k1", "v1");
        assertEquals(1, cache.size());
        assertEquals("v1", cache.get("k1"));
    }
}
