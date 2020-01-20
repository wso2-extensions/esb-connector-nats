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
import io.nats.client.Nats;
import io.nats.client.Options;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.util.Properties;

/**
 * The NATS publisher connection
 */
public class NatsConnection {

    /**
     * Create a new connection to the NATS server.
     *
     * @param messageContext the message context.
     * @return the publisher connection.
     */
    Connection createConnection(MessageContext messageContext) throws IOException, InterruptedException  {
        Axis2MessageContext axis2mc = (Axis2MessageContext) messageContext;
        String servers = (String) axis2mc.getAxis2MessageContext().getProperty(NatsConstants.SERVERS);
        String username = (String) messageContext.getProperty(NatsConstants.USERNAME);
        String password = (String) messageContext.getProperty(NatsConstants.PASSWORD);
        String tlsProtocol = (String) messageContext.getProperty(NatsConstants.TLS_PROTOCOL);
        String tlsKeyStoreType = (String) messageContext.getProperty(NatsConstants.TLS_KEYSTORE_TYPE);
        String tlsKeyStoreLocation = (String) messageContext.getProperty(NatsConstants.TLS_KEYSTORE_LOCATION);
        String tlsKeyStorePassword = (String) messageContext.getProperty(NatsConstants.TLS_KEYSTORE_PASSWORD);
        String tlsTrustStoreType = (String) messageContext.getProperty(NatsConstants.TLS_TRUSTSTORE_TYPE);
        String tlsTrustStoreLocation = (String) messageContext.getProperty(NatsConstants.TLS_TRUSTSTORE_LOCATION);
        String tlsTrustStorePassword = (String) messageContext.getProperty(NatsConstants.TLS_TRUSTSTORE_PASSWORD);
        String tlsKeyManagerAlgorithm = (String) messageContext.getProperty(NatsConstants.TLS_KEY_MANAGER_ALGORITHM);
        String tlsTrustManagerAlgorithm = (String) messageContext.getProperty(NatsConstants.TLS_TRUST_MANAGER_ALGORITHM);
        String bufferSize = (String) messageContext.getProperty(NatsConstants.BUFFER_SIZE);
        String connectionName = (String) messageContext.getProperty(NatsConstants.CONNECTION_NAME);
        String connectionTimeout = (String) messageContext.getProperty(NatsConstants.CONNECTION_TIMEOUT);
        String inboxPrefix = (String) messageContext.getProperty(NatsConstants.INBOX_PREFIX);
        String dataPortType = (String) messageContext.getProperty(NatsConstants.DATA_PORT_TYPE);
        String maxControlLine = (String) messageContext.getProperty(NatsConstants.MAX_CONTROL_LINE);
        String maxPingsOut = (String) messageContext.getProperty(NatsConstants.MAX_PINGS_OUT);
        String maxReconnects = (String) messageContext.getProperty(NatsConstants.MAX_RECONNECTS);
        String pingInterval = (String) messageContext.getProperty(NatsConstants.PING_INTERVAL);
        String reconnectBufferSize = (String) messageContext.getProperty(NatsConstants.RECONNECT_BUFFER_SIZE);
        String reconnectWait = (String) messageContext.getProperty(NatsConstants.RECONNECT_WAIT);
        String requestCleanUpInterval = (String) messageContext.getProperty(NatsConstants.REQUEST_CLEANUP_INTERVAL);
        String verbose = (String) messageContext.getProperty(NatsConstants.VERBOSE);
        String pedantic = (String) messageContext.getProperty(NatsConstants.PEDANTIC);
        String supportUtf8Subjects = (String) messageContext.getProperty(NatsConstants.SUPPORT_UTF8_SUBJECTS);
        String turnOnAdvancedStats = (String) messageContext.getProperty(NatsConstants.TURN_ON_ADVANCED_STATS);
        String traceConnection = (String) messageContext.getProperty(NatsConstants.TRACE_CONNECTION);
        String useOldRequestStyle = (String) messageContext.getProperty(NatsConstants.USE_OLD_REQUEST_STYLE);
        String noRandomize = (String) messageContext.getProperty(NatsConstants.NO_RANDOMIZE);
        String noEcho = (String) messageContext.getProperty(NatsConstants.NO_ECHO);

        Properties serverConfig = new Properties();

        // Default values for these properties are empty strings
        serverConfig.setProperty(Options.PROP_SERVERS, servers);
        serverConfig.setProperty(Options.PROP_USERNAME, username);
        serverConfig.setProperty(Options.PROP_PASSWORD, password);
        serverConfig.setProperty(Options.PROP_CONNECTION_NAME, connectionName);
        serverConfig.setProperty(Options.PROP_VERBOSE, verbose);
        serverConfig.setProperty(Options.PROP_PEDANTIC, pedantic);
        serverConfig.setProperty(Options.PROP_UTF8_SUBJECTS, supportUtf8Subjects);
        serverConfig.setProperty(Options.PROP_USE_OLD_REQUEST_STYLE, useOldRequestStyle);
        serverConfig.setProperty(Options.PROP_NORANDOMIZE, noRandomize);
        serverConfig.setProperty(Options.PROP_NO_ECHO, noEcho);

        // Setting these properties with null will throw NullPointerException or setting them to an empty string will cause errors
        setServerConfigProperty(serverConfig, Options.PROP_INBOX_PREFIX, inboxPrefix);
        setServerConfigProperty(serverConfig, Options.PROP_RECONNECT_BUF_SIZE, reconnectBufferSize);
        setServerConfigProperty(serverConfig, Options.PROP_RECONNECT_WAIT, reconnectWait);
        setServerConfigProperty(serverConfig, Options.PROP_MAX_RECONNECT, maxReconnects);
        setServerConfigProperty(serverConfig, Options.PROP_CONNECTION_TIMEOUT, connectionTimeout);
        setServerConfigProperty(serverConfig, Options.PROP_MAX_CONTROL_LINE, maxControlLine);
        setServerConfigProperty(serverConfig, Options.PROP_PING_INTERVAL, pingInterval);
        setServerConfigProperty(serverConfig, Options.PROP_CLEANUP_INTERVAL, requestCleanUpInterval);
        setServerConfigProperty(serverConfig, Options.PROP_MAX_PINGS, maxPingsOut);
        setServerConfigProperty(serverConfig, Options.PROP_DATA_PORT_TYPE, dataPortType);

        Options.Builder builder = new Options.Builder(serverConfig);

        if (StringUtils.isNotEmpty(bufferSize)) {
            builder.bufferSize(Integer.parseInt(bufferSize));
        }

        if (Boolean.parseBoolean(turnOnAdvancedStats)) {
            builder.turnOnAdvancedStats();
        }

        if (Boolean.parseBoolean(traceConnection)) {
            builder.traceConnection();
        }

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
            if (StringUtils.isEmpty(tlsConnection.getKeyStoreLocation())) {
                KeyStore keyStore = loadKeyStore(tlsConnection.getKeyStoreType(), tlsConnection.getKeyStoreLocation(), tlsConnection.getTrustStorePassword());
                keyManagerFactory = KeyManagerFactory.getInstance(tlsConnection.getKeyManagerAlgorithm().equals("") ? NatsConstants.DEFAULT_TLS_ALGORITHM : tlsConnection.getKeyManagerAlgorithm());
                keyManagerFactory.init(keyStore, tlsConnection.getKeyStorePassword().toCharArray());
            }

            KeyStore trustStore = loadKeyStore(tlsConnection.getTrustStoreType(), tlsConnection.getTrustStoreLocation(), tlsConnection.getTrustStorePassword());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tlsConnection.getTrustManagerAlgorithm().equals("") ? NatsConstants.DEFAULT_TLS_ALGORITHM : tlsConnection.getTrustManagerAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance(tlsConnection.getProtocol().equals("") ? Options.DEFAULT_SSL_PROTOCOL : tlsConnection.getProtocol());
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
        KeyStore store = KeyStore.getInstance(storeType.equals("") ? NatsConstants.DEFAULT_STORE_TYPE : storeType);
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
