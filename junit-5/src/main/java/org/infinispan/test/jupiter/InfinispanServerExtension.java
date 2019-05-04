package org.infinispan.test.jupiter;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.server.core.admin.embeddedserver.EmbeddedServerAdminOperationHandler;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.configuration.HotRodServerConfiguration;
import org.infinispan.server.hotrod.configuration.HotRodServerConfigurationBuilder;
import org.infinispan.server.hotrod.test.HotRodTestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.infinispan.test.fwk.TestResourceTracker;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Junit 5 simple extension for the hotrod server
 *
 * @author Katia Aresti, karesti@redhat.com
 */
public class InfinispanServerExtension implements Extension, BeforeTestExecutionCallback, AfterTestExecutionCallback{

    private HotRodServer hotRodServer;
    private RemoteCacheManager hotRodClient;
    private final String host;
    private final int port;
    private final String[] initialCaches;

    public InfinispanServerExtension(String host, int port, String[] initialCaches) {
        this.host = host;
        this.port = port;
        this.initialCaches = initialCaches;
    }

    public static final InfinispanServerExtensionBuilder builder() {
        return new InfinispanServerExtensionBuilder();

    }

    public static class InfinispanServerExtensionBuilder {
        private String host = "localhost";
        private int port = 11222;
        private String[] cacheNames = new String[0];

        public InfinispanServerExtensionBuilder host(String host) {
            this.host = host;
            return this;
        }

        public InfinispanServerExtensionBuilder port(int port) {
            this.port = port;
            return this;
        }

        public InfinispanServerExtensionBuilder withCaches(String... cacheName) {
            this.cacheNames = cacheName;
            return this;
        }

        public InfinispanServerExtension build() {
            return new InfinispanServerExtension(host, port, cacheNames);
        }
    }

    public RemoteCacheManager hotRodClient() {
        if (hotRodServer != null && hotRodClient == null) {
            org.infinispan.client.hotrod.configuration.ConfigurationBuilder builder = new org.infinispan.client.hotrod.configuration.ConfigurationBuilder();
            HotRodServerConfiguration serverConfiguration = hotRodServer.getConfiguration();
            builder.addServer().host(serverConfiguration.publicHost())
                    .port(serverConfiguration.publicPort());
            hotRodClient = new RemoteCacheManager(builder.build());
        }
        return hotRodClient;
    }

    public void start() {
        if (hotRodServer == null) {
            TestResourceTracker.setThreadTestName("InfinispanServer");
            EmbeddedCacheManager ecm = TestCacheManagerFactory.createCacheManager(
                    new GlobalConfigurationBuilder().nonClusteredDefault().defaultCacheName("default"),
                    new ConfigurationBuilder());

            for(String cacheName: initialCaches) {
                ecm.createCache(cacheName, new ConfigurationBuilder().build());
            }
            HotRodServerConfigurationBuilder serverBuilder = new HotRodServerConfigurationBuilder();
            serverBuilder.adminOperationsHandler(new EmbeddedServerAdminOperationHandler());
            hotRodServer = HotRodTestingUtil.startHotRodServer(ecm, host, port, 0, serverBuilder);
        }
    }

    public void stop() {
        if (hotRodServer != null) {
            hotRodServer.stop();
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        start();
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        stop();
    }

}
