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

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.streaming.Options;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.esb.connector.nats_core.NatsConnection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.Properties;

/**
 * The NATS publisher connection
 */
class NatsStreamingConnection {

    /**
     * Create a new connection to the NATS server.
     *
     * @param messageContext the message context.
     * @return the publisher connection.
     */
    StreamingConnection createConnection(MessageContext messageContext) throws IOException, InterruptedException {
        Axis2MessageContext axis2mc = (Axis2MessageContext) messageContext;
        String url = (String) axis2mc.getAxis2MessageContext().getProperty(NatsStreamingConstants.URL);
        String clientId = (String) messageContext.getProperty(NatsStreamingConstants.CLIENT_ID);
        String clusterId = (String) messageContext.getProperty(NatsStreamingConstants.CLUSTER_ID);
        String maxPubAcksInFlight = (String) messageContext.getProperty(NatsStreamingConstants.MAX_PUB_ACKS_IN_FLIGHT);
        String pubAckWait = (String) messageContext.getProperty(NatsStreamingConstants.PUB_ACK_WAIT);
        String connectWait = (String) messageContext.getProperty(NatsStreamingConstants.CONNECT_WAIT);
        String discoverPrefix = (String) messageContext.getProperty(NatsStreamingConstants.DISCOVER_PREFIX);
        String maxPingsOut = (String) messageContext.getProperty(NatsStreamingConstants.MAX_PINGS_OUT);
        String pingInterval = (String) messageContext.getProperty(NatsStreamingConstants.PING_INTERVAL);
        String traceConnection = (String) messageContext.getProperty(NatsStreamingConstants.TRACE_CONNECTION);

        Options.Builder builder = new Options.Builder().natsUrl(url).clusterId(clusterId).clientId(clientId);

        if (Boolean.parseBoolean((String) messageContext.getProperty(NatsStreamingConstants.USE_CORE_NATS_CONNECTION))) {
            builder.natsConn(createNatsConnection(messageContext));
        }

        if (maxPubAcksInFlight != null) {
            builder.maxPubAcksInFlight(Integer.parseInt(maxPubAcksInFlight));
        }

        if (pubAckWait != null) {
            builder.pubAckWait(Duration.ofSeconds(Integer.parseInt(pubAckWait)));
        }

        if (connectWait != null) {
            builder.connectWait(Duration.ofSeconds(Integer.parseInt(connectWait)));
        }

        if (discoverPrefix != null) {
            builder.discoverPrefix(discoverPrefix);
        }

        if (maxPingsOut != null) {
            builder.maxPingsOut(Integer.parseInt(maxPingsOut));
        }

        if (pingInterval != null) {
            builder.pingInterval(Duration.ofSeconds(Integer.parseInt(pingInterval)));
        }

        if (Boolean.parseBoolean(traceConnection)) {
            builder.traceConnection();
        }

        StreamingConnectionFactory streamingConnectionFactory = new StreamingConnectionFactory(builder.build());
        return streamingConnectionFactory.createConnection();
    }

    private Connection createNatsConnection(MessageContext messageContext) throws IOException, InterruptedException {
        String natsUrl = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_URL);
        String username = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_USERNAME);
        String password = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_PASSWORD);
        String tlsProtocol = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_PROTOCOL);
        String tlsKeyStoreType = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_KEYSTORE_TYPE);
        String tlsKeyStoreLocation = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_KEYSTORE_LOCATION);
        String tlsKeyStorePassword = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_KEYSTORE_PASSWORD);
        String tlsTrustStoreType = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUSTSTORE_TYPE);
        String tlsTrustStoreLocation = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUSTSTORE_LOCATION);
        String tlsTrustStorePassword = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUSTSTORE_PASSWORD);
        String tlsKeyManagerAlgorithm = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_KEY_MANAGER_ALGORITHM);
        String tlsTrustManagerAlgorithm = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUST_MANAGER_ALGORITHM);

        Properties serverConfig = new Properties();

        serverConfig.setProperty(io.nats.client.Options.PROP_URL, natsUrl == null ? NatsStreamingConstants.DEFAULT_URL : natsUrl);
        setServerConfigProperty(serverConfig, io.nats.client.Options.PROP_USERNAME, username);
        setServerConfigProperty(serverConfig, io.nats.client.Options.PROP_PASSWORD, password);

        io.nats.client.Options.Builder builder = new io.nats.client.Options.Builder(serverConfig);

        if (StringUtils.isNotEmpty(tlsProtocol + tlsTrustStoreType + tlsTrustStoreLocation + tlsTrustStorePassword + tlsKeyStoreType + tlsKeyStoreLocation + tlsKeyStorePassword + tlsKeyManagerAlgorithm + tlsTrustManagerAlgorithm)) {
            SSLContext sslContext = createSSLContext(new TLSConnection(tlsProtocol, tlsTrustStoreType, tlsTrustStoreLocation, tlsTrustStorePassword, tlsKeyStoreType, tlsKeyStoreLocation, tlsKeyStorePassword, tlsKeyManagerAlgorithm, tlsTrustManagerAlgorithm));
            if (sslContext != null) {
                builder.sslContext(sslContext);
            }
        }
        return Nats.connect(builder.build());
    }

    /**
     * Check whether the parameter provided is empty or not.
     *
     * @param serverConfig the server configuration properties object.
     * @param property the property to set.
     * @param parameter the parameter value to set to the property.
     */
    private void setServerConfigProperty(Properties serverConfig, String property, String parameter) {
        if (StringUtils.isNotEmpty(parameter)) {
            serverConfig.setProperty(property, parameter);
        }
    } 
    
    /**
     * Create the SSLContext to establish connection with TLS.
     *
     * @param tlsConnection the TLS connection object.
     * @return the SSLContext or null if any exceptions.
     */
    private static SSLContext createSSLContext(TLSConnection tlsConnection) {
        Log log = LogFactory.getLog(NatsConnection.class);
        try {
            KeyManagerFactory keyManagerFactory = null;
            if (StringUtils.isNotEmpty(tlsConnection.getKeyStoreLocation())) {
                KeyStore keyStore = loadKeyStore(tlsConnection.getKeyStoreType(), tlsConnection.getKeyStoreLocation(), tlsConnection.getTrustStorePassword());
                keyManagerFactory = KeyManagerFactory.getInstance(tlsConnection.getKeyManagerAlgorithm().equals("") ? NatsStreamingConstants.DEFAULT_TLS_ALGORITHM : tlsConnection.getKeyManagerAlgorithm());
                keyManagerFactory.init(keyStore, tlsConnection.getKeyStorePassword().toCharArray());
            }

            KeyStore trustStore = loadKeyStore(tlsConnection.getTrustStoreType(), tlsConnection.getTrustStoreLocation(), tlsConnection.getTrustStorePassword());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tlsConnection.getTrustManagerAlgorithm().equals("") ? NatsStreamingConstants.DEFAULT_TLS_ALGORITHM : tlsConnection.getTrustManagerAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance(tlsConnection.getProtocol().equals("") ? io.nats.client.Options.DEFAULT_SSL_PROTOCOL : tlsConnection.getProtocol());
            sslContext.init(keyManagerFactory == null ? null : keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext;
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException e) {
            log.error("Invalid TLS parameters. Establishing connection without TLS if possible.", e);
            return null;
        }
    }

    /**
     * Load key store and trust store.
     *
     * @param storeType the type of store file.
     * @param storeLocation the location of store file.
     * @param trustStorePassword the password of trust store file.
     * @return the store.
     */
    private static KeyStore loadKeyStore(String storeType, String storeLocation, String trustStorePassword) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore store = KeyStore.getInstance(storeType.equals("") ? NatsStreamingConstants.DEFAULT_STORE_TYPE : storeType);
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(storeLocation))) {
            store.load(in, trustStorePassword.toCharArray());
        }
        return store;
    }
}


/**
 * Set the TLS connection properties to connect to the server with TLS.
 */
class TLSConnection {
    private String protocol;
    private String trustStoreType;
    private String trustStoreLocation;
    private String trustStorePassword;
    private String keyStoreType;
    private String keyStoreLocation;
    private String keyStorePassword;
    private String keyManagerAlgorithm;
    private String trustManagerAlgorithm;

    TLSConnection(String protocol, String trustStoreType, String trustStoreLocation, String trustStorePassword, String keyStoreType, String keyStoreLocation, String keyStorePassword, String keyManagerAlgorithm, String trustManagerAlgorithm) {
        this.protocol = protocol;
        this.trustStoreType = trustStoreType;
        this.trustStoreLocation = trustStoreLocation;
        this.trustStorePassword = trustStorePassword;
        this.keyStoreType = keyStoreType;
        this.keyStoreLocation = keyStoreLocation;
        this.keyStorePassword = keyStorePassword;
        this.keyManagerAlgorithm = keyManagerAlgorithm;
        this.trustManagerAlgorithm = trustManagerAlgorithm;
    }

    String getProtocol() {
        return protocol;
    }

    String getTrustStoreType() {
        return trustStoreType;
    }

    String getTrustStoreLocation() {
        return trustStoreLocation;
    }

    String getTrustStorePassword() {
        return trustStorePassword;
    }

    String getKeyStoreType() {
        return keyStoreType;
    }

    String getKeyStoreLocation() {
        return keyStoreLocation;
    }

    String getKeyStorePassword() {
        return keyStorePassword;
    }

    String getKeyManagerAlgorithm() {
        return keyManagerAlgorithm;
    }

    String getTrustManagerAlgorithm() {
        return trustManagerAlgorithm;
    }
}
