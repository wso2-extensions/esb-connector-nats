/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb.connector.nats_core;

import io.nats.client.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Connection pool for NATS publisher connections.
 */
class NatsConnectionPool {

    private static Log log = LogFactory.getLog(NatsConnectionPool.class);
    private static List<Connection> connectionPool = new ArrayList<>();
    private static List<Connection> connectionsBeingUsed = new ArrayList<>();
    private static NatsConnectionPool natsConnectionPool;

    private NatsConnectionPool() {}

    /**
     * Create a connection for the pool.
     *
     * @param messageContext the message context.
     */
    synchronized void createConnectionForPool(MessageContext messageContext) throws IOException, InterruptedException {
        // If there are no connections in the pool to be used and maximum pool size has not been reached, a connection will be created and added to the pool.
        if (!isConnectionPoolFull(messageContext) && connectionPool.size() == 0) {
            connectionPool.add(new NatsConnection().createConnection(messageContext));
            if (log.isDebugEnabled()) {
                log.debug("Connection added to connection pool.");
            }
        }
    }

    /**
     * Check if connection pool exceeds maximum pool size.
     *
     * @param messageContext the message context.
     * @return true or false.
     */
    private boolean isConnectionPoolFull(MessageContext messageContext) {
        String maxPoolSize = (String) messageContext.getProperty(NatsConstants.MAX_CONNECTION_POOL_SIZE);
        if (log.isDebugEnabled()) {
            log.debug("Maximum pool size is " + maxPoolSize);
        }
        return getConnectionPoolSize() >= Integer.parseInt(maxPoolSize);
    }

    /**
     * Get the connection pool size.
     *
     * @return connection pool size.
     */
    private int getConnectionPoolSize() {
        return connectionPool.size() + connectionsBeingUsed.size();
    }

    /**
     * Get a connection if there is an unused connection in the pool.
     *
     * @return the connection or null.
     */
    synchronized Connection getConnectionFromPool() throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Get a NATS connection from the connection pool.");
        }
        if (connectionPool.size() > 0) {
            Connection connection = connectionPool.remove(0);
            connectionsBeingUsed.add(connection);
            return connection;
        }
        wait();
        return null;
    }

    /**
     * Once the connection is used, put the connection back to the connection pool.
     *
     * @param connection the publisher connection.
     */
    synchronized void putConnectionBackToPool(Connection connection) {
        if (log.isDebugEnabled()) {
            log.debug("Put the NATS connection back to connection pool.");
        }
        connectionPool.add(connection);
        connectionsBeingUsed.remove(connection);
        notify();
    }

    /**
     * Get the NatsConnectionPool object instance.
     *
     * @return NatsConnectionPool instance.
     */
    static synchronized NatsConnectionPool getNatsConnectionPoolInstance() {
        if(natsConnectionPool == null) {
            natsConnectionPool = new NatsConnectionPool();
        }
        return natsConnectionPool;
    }
}
