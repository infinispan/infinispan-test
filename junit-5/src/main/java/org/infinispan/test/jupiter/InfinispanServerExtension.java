package org.infinispan.test.jupiter;

import java.util.Collections;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.configuration.HotRodServerConfiguration;
import org.infinispan.server.hotrod.test.HotRodTestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.infinispan.test.fwk.TestResourceTracker;
import org.junit.jupiter.api.extension.Extension;

/**
 * Junit 5 simple extension for the hotrod server
 *
 * @author Katia Aresti, karesti@redhat.com
 */
public class InfinispanServerExtension implements Extension {

    private HotRodServer hotRodServer;
    private RemoteCacheManager hotRodClient;

    public Map<String, String> start() {
        TestResourceTracker.setThreadTestName("InfinispanServer");
        EmbeddedCacheManager ecm = TestCacheManagerFactory.createCacheManager(
                new GlobalConfigurationBuilder().nonClusteredDefault().defaultCacheName("default"),
                new ConfigurationBuilder());
        // Client connects to a non default port
        hotRodServer = HotRodTestingUtil.startHotRodServer(ecm, 11222);
        return Collections.emptyMap();
    }

    public void stop() {
        if (hotRodServer != null) {
            hotRodServer.stop();
        }
    }

    public RemoteCacheManager hotRodClient() {
        if (hotRodClient == null) {
            org.infinispan.client.hotrod.configuration.ConfigurationBuilder builder = new org.infinispan.client.hotrod.configuration.ConfigurationBuilder();
            HotRodServerConfiguration serverConfiguration = hotRodServer.getConfiguration();
            builder.addServer().host(serverConfiguration.publicHost())
                    .port(serverConfiguration.publicPort());
            hotRodClient = new RemoteCacheManager(builder.build());
        }
        return hotRodClient;
    }
}
