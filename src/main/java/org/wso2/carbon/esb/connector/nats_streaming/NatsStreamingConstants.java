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

class NatsStreamingConstants {

    private NatsStreamingConstants() {}

    // Configuration properties for streamingSendMessage operation
    static final String SUBJECT = "subject";
    static final String METHOD_NAME = "streamingSendMessage:";

    // Configuration properties for streamingInit operation
    static final String URL = "nats.streaming.url";
    static final String CLIENT_ID = "nats.streaming.clientId";
    static final String CLUSTER_ID = "nats.streaming.clusterId";
    static final String USE_CORE_NATS_CONNECTION = "use.core.nats.connection";
    static final String MAX_PUB_ACKS_IN_FLIGHT = "nats.streaming.maxPubAcksInFlight";
    static final String PUB_ACK_WAIT = "nats.streaming.pubAckWait";
    static final String CONNECT_WAIT = "nats.streaming.connectWait";
    static final String DISCOVER_PREFIX = "nats.streaming.discoverPrefix";
    static final String MAX_PINGS_OUT = "nats.streaming.maxPingsOut";
    static final String PING_INTERVAL = "nats.streaming.pingInterval";
    static final String TRACE_CONNECTION = "nats.streaming.traceConnection";
    static final String MAX_CONNECTION_POOL_SIZE = "nats.streaming.maxPoolSize";

    // Core NATS configuration properties if using a custom Core NATS connection for streamingInit properties
    static final String NATS_CORE_URL = "nats.core.url";
    static final String NATS_CORE_USERNAME = "nats.core.username";
    static final String NATS_CORE_PASSWORD = "nats.core.password";
    static final String NATS_CORE_TLS_PROTOCOL = "nats.core.tlsProtocol";
    static final String NATS_CORE_TLS_KEYSTORE_TYPE = "nats.core.tlsKeyStoreType";
    static final String NATS_CORE_TLS_KEYSTORE_LOCATION = "nats.core.tlsKeyStoreLocation";
    static final String NATS_CORE_TLS_KEYSTORE_PASSWORD = "nats.core.tlsKeyStorePassword";
    static final String NATS_CORE_TLS_TRUSTSTORE_TYPE = "nats.core.tlsTrustStoreType";
    static final String NATS_CORE_TLS_TRUSTSTORE_LOCATION = "nats.core.tlsTrustStoreLocation";
    static final String NATS_CORE_TLS_TRUSTSTORE_PASSWORD = "nats.core.tlsTrustStorePassword";
    static final String NATS_CORE_TLS_KEY_MANAGER_ALGORITHM = "nats.core.tlsKeyManagerAlgorithm";
    static final String NATS_CORE_TLS_TRUST_MANAGER_ALGORITHM = "nats.core.tlsTrustManagerAlgorithm";
    static final String NATS_CORE_BUFFER_SIZE = "nats.core.bufferSize";
    static final String NATS_CORE_CONNECTION_NAME = "nats.core.connectionName";
    static final String NATS_CORE_CONNECTION_TIMEOUT = "nats.core.connectionTimeout";
    static final String NATS_CORE_DATA_PORT_TYPE = "nats.core.dataPortType";
    static final String NATS_CORE_INBOX_PREFIX = "nats.core.inboxPrefix";
    static final String NATS_CORE_MAX_CONTROL_LINE = "nats.core.maxControlLine";
    static final String NATS_CORE_MAX_PINGS_OUT = "nats.core.maxPingsOut";
    static final String NATS_CORE_MAX_RECONNECTS = "nats.core.maxReconnects";
    static final String NATS_CORE_PING_INTERVAL = "nats.core.pingInterval";
    static final String NATS_CORE_RECONNECT_BUFFER_SIZE = "nats.core.reconnectBufferSize";
    static final String NATS_CORE_RECONNECT_WAIT = "nats.core.reconnectWait";
    static final String NATS_CORE_REQUEST_CLEANUP_INTERVAL = "nats.core.requestCleanUpInterval";
    static final String NATS_CORE_VERBOSE = "nats.core.verbose";
    static final String NATS_CORE_PEDANTIC = "nats.core.pedantic";
    static final String NATS_CORE_SUPPORT_UTF8_SUBJECTS = "nats.core.supportUTF8Subjects";
    static final String NATS_CORE_TURN_ON_ADVANCED_STATS = "nats.core.turnOnAdvancedStats";
    static final String NATS_CORE_TRACE_CONNECTION = "nats.core.traceConnection";
    static final String NATS_CORE_USE_OLD_REQUEST_STYLE = "nats.core.useOldRequestStyle";
    static final String NATS_CORE_NO_RANDOMIZE = "nats.core.noRandomize";
    static final String NATS_CORE_NO_ECHO = "nats.core.noEcho";

    // Default values
    static final String DEFAULT_URL = "nats://localhost:4222";
    static final String DEFAULT_CLUSTER_ID = "test-cluster";
    static final String DEFAULT_CONNECTION_POOL_SIZE = "5";
    static final String DEFAULT_TLS_ALGORITHM = "SunX509";
    static final String DEFAULT_STORE_TYPE = "JKS";
}
