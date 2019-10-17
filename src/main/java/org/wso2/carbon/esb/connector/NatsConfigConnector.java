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

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;

/**
 * NATS configuration for certain parameters
 */
public class NatsConfigConnector extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) {
        String servers = (String) messageContext.getProperty(NatsConstants.SERVERS);
        // Setting null for these parameters will result in NullPointerException
        String username = validateParameter((String) messageContext.getProperty(NatsConstants.USERNAME));
        String password = validateParameter((String) messageContext.getProperty(NatsConstants.PASSWORD));
        String tlsProtocol = validateParameter((String) messageContext.getProperty(NatsConstants.TLS_PROTOCOL));
        String tlsKeyStoreType = validateParameter((String) messageContext.getProperty(NatsConstants.TLS_KEYSTORE_TYPE));
        String tlsKeyStoreLocation = validateParameter((String) messageContext.getProperty(NatsConstants.TLS_KEYSTORE_LOCATION));
        String tlsKeyStorePassword = validateParameter((String) messageContext.getProperty(NatsConstants.TLS_KEYSTORE_PASSWORD));
        String tlsTrustStoreType = validateParameter((String) messageContext.getProperty(NatsConstants.TLS_TRUSTSTORE_TYPE));
        String tlsTrustStoreLocation = validateParameter((String) messageContext.getProperty(NatsConstants.TLS_TRUSTSTORE_LOCATION));
        String tlsTrustStorePassword = validateParameter((String) messageContext.getProperty(NatsConstants.TLS_TRUSTSTORE_PASSWORD));
        String tlsKeyManagerAlgorithm = validateParameter((String) messageContext.getProperty(NatsConstants.TLS_KEY_MANAGER_ALGORITHM));
        String tlsTrustManagerAlgorithm = validateParameter((String) messageContext.getProperty(NatsConstants.TLS_TRUST_MANAGER_ALGORITHM));
        String connectionName = validateParameter((String) messageContext.getProperty(NatsConstants.CONNECTION_NAME));
        String verbose = validateParameter((String) messageContext.getProperty(NatsConstants.VERBOSE));
        String pedantic = validateParameter((String) messageContext.getProperty(NatsConstants.PEDANTIC));
        String supportUtf8Subjects = validateParameter((String) messageContext.getProperty(NatsConstants.SUPPORT_UTF8_SUBJECTS));
        String useOldRequestStyle = validateParameter((String) messageContext.getProperty(NatsConstants.USE_OLD_REQUEST_STYLE));
        String noRandomize = validateParameter((String) messageContext.getProperty(NatsConstants.NO_RANDOMIZE));
        String noEcho = validateParameter((String) messageContext.getProperty(NatsConstants.NO_ECHO));
        String maxPoolSize = validateMaxPoolSize((String) messageContext.getProperty(NatsConstants.MAX_CONNECTION_POOL_SIZE));

        messageContext.setProperty(NatsConstants.SERVERS, servers == null ? NatsConstants.DEFAULT_NATS_SERVER_URL : servers);
        messageContext.setProperty(NatsConstants.USERNAME, username);
        messageContext.setProperty(NatsConstants.PASSWORD, password);
        messageContext.setProperty(NatsConstants.TLS_PROTOCOL, tlsProtocol);
        messageContext.setProperty(NatsConstants.TLS_KEYSTORE_TYPE, tlsKeyStoreType);
        messageContext.setProperty(NatsConstants.TLS_KEYSTORE_LOCATION, tlsKeyStoreLocation);
        messageContext.setProperty(NatsConstants.TLS_KEYSTORE_PASSWORD, tlsKeyStorePassword);
        messageContext.setProperty(NatsConstants.TLS_TRUSTSTORE_TYPE, tlsTrustStoreType);
        messageContext.setProperty(NatsConstants.TLS_TRUSTSTORE_LOCATION, tlsTrustStoreLocation);
        messageContext.setProperty(NatsConstants.TLS_TRUSTSTORE_PASSWORD, tlsTrustStorePassword);
        messageContext.setProperty(NatsConstants.TLS_KEY_MANAGER_ALGORITHM, tlsKeyManagerAlgorithm);
        messageContext.setProperty(NatsConstants.TLS_TRUST_MANAGER_ALGORITHM, tlsTrustManagerAlgorithm);
        messageContext.setProperty(NatsConstants.CONNECTION_NAME, connectionName);
        messageContext.setProperty(NatsConstants.VERBOSE, verbose);
        messageContext.setProperty(NatsConstants.PEDANTIC, pedantic);
        messageContext.setProperty(NatsConstants.SUPPORT_UTF8_SUBJECTS, supportUtf8Subjects);
        messageContext.setProperty(NatsConstants.USE_OLD_REQUEST_STYLE, useOldRequestStyle);
        messageContext.setProperty(NatsConstants.NO_RANDOMIZE, noRandomize);
        messageContext.setProperty(NatsConstants.NO_ECHO, noEcho);
        messageContext.setProperty(NatsConstants.MAX_CONNECTION_POOL_SIZE, maxPoolSize);
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
                maxPoolSize = NatsConstants.DEFAULT_CONNECTION_POOL_SIZE;
            }
        } catch (NumberFormatException e) {
            maxPoolSize = NatsConstants.DEFAULT_CONNECTION_POOL_SIZE;
        }
        return maxPoolSize;
    }
}
