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

import com.google.gson.GsonBuilder;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.NUID;

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
import java.util.concurrent.CountDownLatch;

/**
 * Publish messages to NATS server.
 */
public class NatsPublishConnector extends AbstractConnector {

    private static Log log = LogFactory.getLog(NatsPublishConnector.class);

    @Override public void connect(MessageContext messageContext) {
        try {
            // Get the subject
            String subject = lookupTemplateParameter(messageContext, NatsConstants.SUBJECT);
            String message = getMessage(messageContext);
            NatsConnectionPool connectionPool = NatsConnectionPool.getNatsConnectionPoolInstance();
            // Send the message on the subject with or without response based on if the getResponse parameter is set to true.
            if (isResponseTrue(lookupTemplateParameter(messageContext, NatsConstants.GET_RESPONSE))) {
                sendMessageWithReply(subject, message, messageContext, connectionPool);
            } else {
                sendMessage(subject, message, messageContext, connectionPool);
            }
        } catch (AxisFault | InterruptedException e) {
            log.error("An error occurred while sending the message.", e);
        } catch (IOException e) {
            log.error("An error occurred while connecting to NATS server.", e);
        }
    }

    /**
     * Send the message and wait for the response.
     *
     * @param subject        the subject to publish the message.
     * @param message        the message to publish.
     * @param messageContext the message context.
     * @param connectionPool the NatsConnectionPool instance to get a connection from the pool if available.
     */
    private void sendMessageWithReply(String subject, String message, MessageContext messageContext, NatsConnectionPool connectionPool) throws IOException, InterruptedException {
        Connection publisher = getConnectionFromConnectionPool(connectionPool, messageContext);
        String replySubject = NUID.nextGlobal();
        CountDownLatch latch = new CountDownLatch(1);
        Dispatcher dispatcher = publisher.createDispatcher((msg) -> {
            log.info("Message Sent: " + message + "Acknowledgment: " + new String(msg.getData(), StandardCharsets.UTF_8) + "\n");
            latch.countDown();
        });
        dispatcher.subscribe(replySubject);
        publisher.publish(subject, replySubject, natsMessageWithHeaders(new NatsMessage(message, getDynamicParameters(messageContext, subject))).getBytes(StandardCharsets.UTF_8));
        dispatcher.unsubscribe(replySubject, 1);
        latch.await();
        connectionPool.putConnectionBackToPool(publisher);
    }

    /**
     * Send the message (fire and forget).
     *
     * @param subject        The subject to publish the message.
     * @param message        The message to publish.
     * @param messageContext The message context.
     * @param connectionPool The NatsConnectionPool instance to get a connection from the pool if available.
     */
    private void sendMessage(String subject, String message, MessageContext messageContext, NatsConnectionPool connectionPool) throws IOException, InterruptedException {
        Connection publisher = getConnectionFromConnectionPool(connectionPool, messageContext);
        publisher.publish(subject, natsMessageWithHeaders(new NatsMessage(message, getDynamicParameters(messageContext, subject))).getBytes(StandardCharsets.UTF_8));
        connectionPool.putConnectionBackToPool(publisher);
        log.info("Message Sent: " + message);
    }

    /**
     * Get a connection from the connection pool.
     *
     * @param messageContext The message context.
     * @param connectionPool The NatsConnectionPool instance.
     * @return a connection from the connection pool
     */
    private Connection getConnectionFromConnectionPool(NatsConnectionPool connectionPool, MessageContext messageContext) throws IOException, InterruptedException {
        connectionPool.createConnectionForPool(messageContext);
        Connection publisher = connectionPool.getConnectionFromPool();
        return (publisher == null) ? connectionPool.getConnectionFromPool() : publisher;
    }

    /**
     * Check if getResponse parameter is true.
     *
     * @param responseParam The getResponse parameter value.
     * @return true or false.
     */
    private boolean isResponseTrue(String responseParam) {
        return Boolean.parseBoolean(responseParam);
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
        String key = NatsConstants.METHOD_NAME + subject;
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
    private static String natsMessageWithHeaders(NatsMessage message) {
        return new GsonBuilder().create().toJson(message);
    }
}

/**
 * Configure the message with the payload and headers and send to NATS Server.
 */
class NatsMessage {
    private String payload;
    private Map<String, String> headers;

    NatsMessage(String payload, Map<String, String> headers) {
        this.payload = payload;
        this.headers = headers;
    }
}
