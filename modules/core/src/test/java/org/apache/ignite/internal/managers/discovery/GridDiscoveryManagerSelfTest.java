/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.managers.discovery;

import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;

import static org.apache.ignite.cache.CacheMode.PARTITIONED;

/**
 *
 */
public abstract class GridDiscoveryManagerSelfTest extends GridCommonAbstractTest {
    /** */
    private static final String CACHE_NAME = "cache";

    /** */
    private static final TcpDiscoveryIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        stopAllGrids();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("IfMayBeConditional")
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        CacheConfiguration ccfg1 = defaultCacheConfiguration();

        ccfg1.setName(CACHE_NAME);

        CacheConfiguration ccfg2 = defaultCacheConfiguration();

        ccfg2.setName(null);

        if (gridName.equals(getTestGridName(1)))
            cfg.setClientMode(true);
        else {
            ccfg1.setNearConfiguration(null);
            ccfg2.setNearConfiguration(null);

            ccfg1.setCacheMode(PARTITIONED);
            ccfg2.setCacheMode(PARTITIONED);

            cfg.setCacheConfiguration(ccfg1, ccfg2);
        }

        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();

        discoverySpi.setIpFinder(IP_FINDER);

        cfg.setDiscoverySpi(discoverySpi);

        return cfg;
    }

    /**
     *
     */
    public static class RegularDiscovery extends GridDiscoveryManagerSelfTest {
        /** {@inheritDoc} */
        @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
            IgniteConfiguration cfg = super.getConfiguration(gridName);

            ((TcpDiscoverySpi)cfg.getDiscoverySpi()).setForceServerMode(true);

            return cfg;
        }
    }

    /**
     *
     */
    public static class ClientDiscovery extends GridDiscoveryManagerSelfTest {
        // No-op.
    }
}
