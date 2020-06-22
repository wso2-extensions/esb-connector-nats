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

import com.google.gson.GsonBuilder;
import io.nats.streaming.StreamingConnection;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.MessageFormatter;
import org.apache.axis2.transport.base.BaseUtils;
import org.apache.axis2.util.MessageProcessorSelector;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.Value;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Publish messages to NATS server.
 */
public class NatsStreamingPublishConnector extends AbstractConnector {

    private static Log log = LogFactory.getLog(NatsStreamingPublishConnector.class);

    @Override public void connect(MessageContext messageContext) {
        try {
            // Get the subject
            String subject = lookupTemplateParameter(messageContext, NatsStreamingConstants.SUBJECT);
            String message = getMessage(messageContext);
            NatsStreamingConnectionPool connectionPool = NatsStreamingConnectionPool.getNatsStreamingConnectionPoolInstance();
            // Send the message on the subject with or without response based on if the getResponse parameter is set to true.
            sendMessage(subject, message, messageContext, connectionPool);
        } catch (AxisFault | InterruptedException e) {
            log.error("An error occurred while sending the message.", e);
        } catch (IOException | TimeoutException e) {
            log.error("An error occurred while connecting to NATS server.", e);
        }
    }

    /**
     * Send the message.
     *
     * @param subject        The subject to publish the message.
     * @param message        The message to publish.
     * @param messageContext The message context.
     * @param connectionPool The NatsStreamingConnectionPool instance to get a connection from the pool if available.
     */
    private void sendMessage(String subject, String message, MessageContext messageContext,
            NatsStreamingConnectionPool connectionPool) throws IOException, InterruptedException, TimeoutException {
        StreamingConnection publisher = getConnectionFromConnectionPool(connectionPool, messageContext);
        publisher.publish(subject,
                natsMessageWithHeaders(new NatsStreamingMessage(message, getDynamicParameters(messageContext, subject)))
                        .getBytes(StandardCharsets.UTF_8));
        connectionPool.putConnectionBackToPool(publisher);
        if (log.isDebugEnabled()) {
            log.debug("Message Sent: " + message);
        }
    }

    /**
     * Get a connection from the connection pool.
     *
     * @param messageContext The message context.
     * @param connectionPool The NatsStreamingConnectionPool instance.
     * @return a connection from the connection pool
     */
    private StreamingConnection getConnectionFromConnectionPool(NatsStreamingConnectionPool connectionPool, MessageContext messageContext)
            throws IOException, InterruptedException {
        connectionPool.createConnectionForPool(messageContext);
        StreamingConnection publisher = connectionPool.getConnectionFromPool();
        return (publisher == null) ? connectionPool.getConnectionFromPool() : publisher;
    }

    /**
     * Get the message from the message context and format the message.
     *
     * @param messageContext the message context.
     * @return message formatted to string.
     */
    private String getMessage(MessageContext messageContext) throws AxisFault {
        Axis2MessageContext axisMsgContext = (Axis2MessageContext) messageContext;
        org.apache.axis2.context.MessageContext msgContext = axisMsgContext.getAxis2MessageContext();
        return formatMessage(msgContext);
    }

    /**
     * Read the value from the input parameter.
     *
     * @param messageContext the message context.
     * @param paramName      the parameter name
     * @return parameter value converted to string.
     */
    private static String lookupTemplateParameter(MessageContext messageContext, String paramName) {
        return (String) ConnectorUtils.lookupTemplateParamater(messageContext, paramName);
    }

    /**
     * Format the messages when the messages are sent to the kafka broker
     *
     * @param messageContext the message context.
     * @return message formatted to string.
     */
    private static String formatMessage(org.apache.axis2.context.MessageContext messageContext) throws AxisFault {
        OMOutputFormat format = BaseUtils.getOMOutputFormat(messageContext);
        MessageFormatter messageFormatter = MessageProcessorSelector.getMessageFormatter(messageContext);
        StringWriter stringWriter = new StringWriter();
        OutputStream out = new WriterOutputStream(stringWriter, format.getCharSetEncoding());
        try {
            messageFormatter.writeTo(messageContext, format, out, true);
        } catch (IOException e) {
            throw new AxisFault("The Error occured while formatting the message", e);
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                throw new AxisFault("The Error occured while closing the output stream", e);
            }
        }
        return stringWriter.toString();
    }

    /**
     * Generate the dynamic parameters from message context parameter.
     *
     * @param messageContext The message context
     * @param subject        the subject to generate the dynamic parameters.
     * @return extract the values from properties and return a Map of keys and values.
     */
    private Map<String, String> getDynamicParameters(MessageContext messageContext, String subject) {
        String key = NatsStreamingConstants.METHOD_NAME + subject;
        Map<String, Object> propertiesMap = (((Axis2MessageContext) messageContext).getProperties());
        Map<String, String> headers = new HashMap<>();
        for (String keyValue : propertiesMap.keySet()) {
            if (keyValue.startsWith(key)) {
                String propertyValue = ((Value) propertiesMap.get(keyValue)).getKeyValue();
                String header = keyValue.substring(key.length() + 1);
                headers.put(header, propertyValue);
            }
        }
        return headers;
    }

    /**
     * Generate the message to publish to the NATS server with payload and headers.
     *
     * @param message the message
     * @return JSON string of NatsMessage
     */
    private static String natsMessageWithHeaders(NatsStreamingMessage message) {
        return new GsonBuilder().create().toJson(message);
    }
}

/**
 * Configure the message with the payload and headers and send to NATS Server.
 */
class NatsStreamingMessage {
    private String payload;
    private Map<String, String> headers;

    NatsStreamingMessage(String payload, Map<String, String> headers) {
        this.payload = payload;
        this.headers = headers;
    }
}
