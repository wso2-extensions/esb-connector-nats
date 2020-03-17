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
package org.wso2.carbon.esb.connector.nats_streaming;

import io.nats.streaming.StreamingConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Connection pool for NATS publisher connections.
 */
class NatsStreamingConnectionPool {

    private static Log log = LogFactory.getLog(NatsStreamingConnectionPool.class);
    private static List<StreamingConnection> connectionPool = new ArrayList<>();
    private static List<StreamingConnection> connectionsBeingUsed = new ArrayList<>();
    private static NatsStreamingConnectionPool natsStreamingConnectionPool;

    private NatsStreamingConnectionPool() {}

    /**
     * Create a connection for the pool.
     *
     * @param messageContext the message context.
     */
    synchronized void createConnectionForPool(MessageContext messageContext) throws IOException, InterruptedException {
        // If there are no connections in the pool to be used and maximum pool size has not been reached, a connection will be created and added to the pool.
        if (!isConnectionPoolFull(messageContext) && connectionPool.size() == 0) {
            connectionPool.add(new NatsStreamingConnection().createConnection(messageContext));
            printDebugLog("Connection added to connection pool.");
        }
    }

    /**
     * Check if connection pool exceeds maximum pool size.
     *
     * @param messageContext the message context.
     * @return true or false.
     */
    private boolean isConnectionPoolFull(MessageContext messageContext) {
        String maxPoolSize = (String) messageContext.getProperty(NatsStreamingConstants.MAX_CONNECTION_POOL_SIZE);
        printDebugLog("Maximum pool size is " + maxPoolSize);
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
    synchronized StreamingConnection getConnectionFromPool() throws InterruptedException {
        printDebugLog("Get a NATS Streaming connection from the connection pool.");
        if (connectionPool.size() > 0) {
            StreamingConnection connection = connectionPool.remove(0);
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
    synchronized void putConnectionBackToPool(StreamingConnection connection) {
        printDebugLog("Put the NATS Streaming connection back to connection pool.");
        connectionPool.add(connection);
        connectionsBeingUsed.remove(connection);
        notify();
    }

    /**
     * Get the NatsConnectionPool object instance.
     *
     * @return NatsConnectionPool instance.
     */
    static synchronized NatsStreamingConnectionPool getNatsStreamingConnectionPoolInstance() {
        if(natsStreamingConnectionPool == null) {
            natsStreamingConnectionPool = new NatsStreamingConnectionPool();
        }
        return natsStreamingConnectionPool;
    }

    /**
     * Check if debug is enabled for logging.
     *
     * @param text log text
     */
    private void printDebugLog(String text) {
        if (log.isDebugEnabled()) {
            log.debug(text);
        }
    }
}
