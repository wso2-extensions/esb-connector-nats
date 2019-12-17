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
package org.wso2.carbon.esb.connector;

class NatsConstants {

    private NatsConstants() {}

    // Configuration properties for sendMessage operation
    static final String SUBJECT = "subject";
    static final String GET_REPLY = "getReply";
    static final String METHOD_NAME = "sendMessage:";

    // Configuration properties for init operation
    static final String SERVERS = "nats.servers";
    static final String USERNAME = "nats.username";
    static final String PASSWORD = "nats.password";
    static final String TLS_PROTOCOL = "nats.tlsProtocol";
    static final String TLS_KEYSTORE_TYPE = "nats.tlsKeyStoreType";
    static final String TLS_KEYSTORE_LOCATION = "nats.tlsKeyStoreLocation";
    static final String TLS_KEYSTORE_PASSWORD = "nats.tlsKeyStorePassword";
    static final String TLS_TRUSTSTORE_TYPE = "nats.tlsTrustStoreType";
    static final String TLS_TRUSTSTORE_LOCATION = "nats.tlsTrustStoreLocation";
    static final String TLS_TRUSTSTORE_PASSWORD = "nats.tlsTrustStorePassword";
    static final String TLS_KEY_MANAGER_ALGORITHM = "nats.tlsKeyManagerAlgorithm";
    static final String TLS_TRUST_MANAGER_ALGORITHM = "nats.tlsTrustManagerAlgorithm";
    static final String BUFFER_SIZE = "nats.bufferSize";
    static final String CONNECTION_NAME = "nats.connectionName";
    static final String CONNECTION_TIMEOUT = "nats.connectionTimeout";
    static final String DATA_PORT_TYPE = "nats.dataPortType";
    static final String INBOX_PREFIX = "nats.inboxPrefix";
    static final String MAX_CONTROL_LINE = "nats.maxControlLine";
    static final String MAX_PINGS_OUT = "nats.maxPingsOut";
    static final String MAX_RECONNECTS = "nats.maxReconnects";
    static final String PING_INTERVAL = "nats.pingInterval";
    static final String RECONNECT_BUFFER_SIZE = "nats.reconnectBufferSize";
    static final String RECONNECT_WAIT = "nats.reconnectWait";
    static final String REQUEST_CLEANUP_INTERVAL = "nats.requestCleanUpInterval";
    static final String VERBOSE = "nats.verbose";
    static final String PEDANTIC = "nats.pedantic";
    static final String SUPPORT_UTF8_SUBJECTS = "nats.supportUTF8Subjects";
    static final String TURN_ON_ADVANCED_STATS = "nats.turnOnAdvancedStats";
    static final String TRACE_CONNECTION = "nats.traceConnection";
    static final String USE_OLD_REQUEST_STYLE = "nats.useOldRequestStyle";
    static final String NO_RANDOMIZE = "nats.noRandomize";
    static final String NO_ECHO = "nats.noEcho";
    static final String MAX_CONNECTION_POOL_SIZE = "nats.maxPoolSize";

    // Default values
    static final String DEFAULT_NATS_SERVER_URL = "nats://localhost:4222";
    static final String DEFAULT_CONNECTION_POOL_SIZE = "5";
    static final String DEFAULT_TLS_ALGORITHM = "SunX509";
    static final String DEFAULT_STORE_TYPE = "JKS";
}
