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

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;

/**
 * NATS configuration for certain parameters
 */
public class NatsStreamingConfigConnector extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) {
        String url = (String) messageContext.getProperty(NatsStreamingConstants.URL);
        String clusterId = (String) messageContext.getProperty(NatsStreamingConstants.CLUSTER_ID);
        String maxPoolSize = validateMaxPoolSize((String) messageContext.getProperty(NatsStreamingConstants.MAX_CONNECTION_POOL_SIZE));

        // Properties for Core NATS connection if using a custm Core NATS connection.
        String natsUrl = (String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_URL);
        String username = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_USERNAME));
        String password = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_PASSWORD));
        String tlsProtocol = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_PROTOCOL));
        String tlsKeyStoreType = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_KEYSTORE_TYPE));
        String tlsKeyStoreLocation = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_KEYSTORE_LOCATION));
        String tlsKeyStorePassword = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_KEYSTORE_PASSWORD));
        String tlsTrustStoreType = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUSTSTORE_TYPE));
        String tlsTrustStoreLocation = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUSTSTORE_LOCATION));
        String tlsTrustStorePassword = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUSTSTORE_PASSWORD));
        String tlsKeyManagerAlgorithm = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_KEY_MANAGER_ALGORITHM));
        String tlsTrustManagerAlgorithm = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUST_MANAGER_ALGORITHM));
        String connectionName = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_CONNECTION_NAME));
        String verbose = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_VERBOSE));
        String pedantic = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_PEDANTIC));
        String supportUtf8Subjects = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_SUPPORT_UTF8_SUBJECTS));
        String useOldRequestStyle = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_USE_OLD_REQUEST_STYLE));
        String noRandomize = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_NO_RANDOMIZE));
        String noEcho = validateParameter((String) messageContext.getProperty(NatsStreamingConstants.NATS_CORE_NO_ECHO));

        messageContext.setProperty(NatsStreamingConstants.URL, url == null ? NatsStreamingConstants.DEFAULT_URL : url);
        messageContext.setProperty(NatsStreamingConstants.CLUSTER_ID, clusterId == null ? NatsStreamingConstants.DEFAULT_CLUSTER_ID : clusterId);

        // Setting properties for Core NATS connection if using a custm Core NATS connection.
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_URL, natsUrl == null ? NatsStreamingConstants.DEFAULT_URL : natsUrl);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_USERNAME, username);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_PASSWORD, password);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_TLS_PROTOCOL, tlsProtocol);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_TLS_KEYSTORE_TYPE, tlsKeyStoreType);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_TLS_KEYSTORE_LOCATION, tlsKeyStoreLocation);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_TLS_KEYSTORE_PASSWORD, tlsKeyStorePassword);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUSTSTORE_TYPE, tlsTrustStoreType);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUSTSTORE_LOCATION, tlsTrustStoreLocation);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUSTSTORE_PASSWORD, tlsTrustStorePassword);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_TLS_KEY_MANAGER_ALGORITHM, tlsKeyManagerAlgorithm);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_TLS_TRUST_MANAGER_ALGORITHM, tlsTrustManagerAlgorithm);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_CONNECTION_NAME, connectionName);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_VERBOSE, verbose);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_PEDANTIC, pedantic);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_SUPPORT_UTF8_SUBJECTS, supportUtf8Subjects);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_USE_OLD_REQUEST_STYLE, useOldRequestStyle);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_NO_RANDOMIZE, noRandomize);
        messageContext.setProperty(NatsStreamingConstants.NATS_CORE_NO_ECHO, noEcho);
        messageContext.setProperty(NatsStreamingConstants.MAX_CONNECTION_POOL_SIZE, maxPoolSize);
    }

    /**
     * Validate null parameter.
     *
     * @param parameter the parameter to validate.
     * @return the updated parameter.
     */
    private String validateParameter(String parameter) {
        return (parameter == null) ? "" : parameter;
    }

    /**
     * Validate maximum pool size parameter.
     *
     * @param maxPoolSize the maximum pool size to validate.
     * @return the updated maximum pool size.
     */
    private String validateMaxPoolSize(String maxPoolSize) {
        try {
            if (Integer.parseInt(maxPoolSize) < 1) {
                maxPoolSize = NatsStreamingConstants.DEFAULT_CONNECTION_POOL_SIZE;
            }
        } catch (NumberFormatException e) {
            maxPoolSize = NatsStreamingConstants.DEFAULT_CONNECTION_POOL_SIZE;
        }
        return maxPoolSize;
    }
}
