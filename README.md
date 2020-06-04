# Core NATS

## Creating a publisher with security

Below is a sample configuration that creates a publisher with security:
```
<nats.init>
    <servers>nats://localhost:4222</servers>
    <tlsKeyStoreLocation><PATH_TO_KEYSTORE_FILE></tlsKeyStoreLocation>
    <tlsKeyStorePassword>password</tlsKeyStorePassword>
    <tlsTrustStoreLocation><PATH_TO_TRUSTSTORE_FILE></tlsTrustStoreLocation>
    <tlsTrustStorePassword>password</tlsTrustStorePassword>
</nats.init>
```
If you want to create a publisher without security, simply provide the ```<servers>``` parameter excluding the other parameters. If the <servers> parameter is
not provided, the connector will automatically connect to nats://localhost:4222. You can connect to multiple 
servers by providing a comma separated list in the ```<servers>``` parameter, for example:

```
<nats.init>
    <servers>nats://localhost:4222, nats://localhost:4223</servers>
</nats.init>
```

## Connecting with a username and password 

Below is a sample configuration that connects to NATS with a username and password:

```
<nats.init>
    <servers>nats://localhost:4222</servers>
    <username>test</username>
    <password>test123</password>
</nats.init>
```
## Creating a connection pool

Below is a sample configuration that creates a connection pool of 10 connections:

```
<nats.init>
    <servers>nats://localhost:4222</servers>
    <maxPoolSize>10</maxPoolSize>
</nats.init>
```
If no value is provided, then a default value of 5 will be used.

After configuring the connection in the <nats.init> element, you can use the connector to send messages. Below are sample
scenarios for sending messages to NATS server.

## Sending a message

Below is a sample configuration that can be used to send a message to NATS server on a subject and receive a Reply from the consumer:

```
<nats.sendMessage>
    <subject>test</subject>
    <getReply>true</getReply>
</nats.sendMessage>
```
Omit the ```<getReply>``` parameter if you do not want a reply. 

## Sending a message with headers

Below is a sample configuration that can be used to send a message to NATS server along with message headers:

```
<nats.sendMessage>
    <subject>test</subject>
    <getReply>true</getReply>
    <test.Content-Type>application/json</test.Content-Type>
</nats.sendMessage>
```
You can provide any number of headers, but the format of the parameter is <SUBJECT_NAME.HEADER>.

## Sending a message to multiple subjects

Below is a sample configuration that can be used to send a message to NATS server on multiple subjects:

```
<nats.sendMessage>
    <subject>test1</subject>
    <getReply>true</getReply>
</nats.sendMessage>
<nats.sendMessage>
    <subject>test2</subject>
    <getReply>true</getReply>
</nats.sendMessage>

```

# NATS Streaming

## Creating a connection

Below is a sample configuration that creates a connection to NATS Streaming server:

```
<nats.streamingInit>
    <url>nats://localhost:4222</url>
    <clientId>client123</clientId>
    <clusterId>cluster123</clusterId>
</nats.streamingInit>
```

You can provide other parameters to the connection as given in the __nats-connector-config.md__ file.

## Creating a connection pool

Below is a sample configuration that creates a connection pool of 10 connections:

```
<nats.streamingInit>
    <url>nats://localhost:4222</url>
    <clientId>client123</clientId>
    <clusterId>cluster123</clusterId>
    <maxPoolSize>10</maxPoolSize>
</nats.streamingInit>
```
If no value is provided, then a default value of 5 will be used.

After configuring the connection in the <nats.streamingInit> element, you can use the connector to send messages. Below are sample
scenarios for sending messages to NATS Streaming server.

## Sending a message

Below is a sample configuration that can be used to send a message to NATS Streaming server on a subject/channel:

```
<nats.streamingSendMessage>
    <subject>test</subject>
</nats.streamingSendMessage>

```

## Sending a message with headers

Below is a sample configuration that can be used to send a message to NATS Streaming server along with message headers:

```
<nats.streamingSendMessage>
    <subject>test</subject>
    <test.Content-Type>application/json</test.Content-Type>
</nats.streamingSendMessage>
```
You can provide any number of headers, but the format of the parameter is <SUBJECT_NAME.HEADER>.

## Sending a message to multiple subjects/channels

Below is a sample configuration that can be used to send a message to NATS Streaming server on multiple subjects:

```
<nats.streamingSendMessage>
    <subject>test1</subject>
</nats.streamingSendMessage>
<nats.streamingSendMessage>
    <subject>test2</subject>
</nats.streamingSendMessage>

```

## Using a core NATS connection

Below are sample configurations if you want to use a custom core NATS connection with the streaming connection. 

### Creating a publisher with security

This may be because you want to establish a secure TLS connection to NATS server as NATS Streaming does not have the option
to configure a connection with TLS.

```
<nats.streamingInit>
    <url>nats://localhost:4222</url>
    <clientId>client123</clientId>
    <clusterId>cluster123</clusterId>
    <useCoreNatsConnection>true</useCoreNatsConnection>
    <natsUrl>nats://localhost:4223</natsUrl>
    <tlsKeyStoreLocation><PATH_TO_KEYSTORE_FILE></tlsKeyStoreLocation>
    <tlsKeyStorePassword>password</tlsKeyStorePassword>
    <tlsTrustStoreLocation><PATH_TO_TRUSTSTORE_FILE></tlsTrustStoreLocation>
    <tlsTrustStorePassword>password</tlsTrustStorePassword>
</nats.streamingInit>
```

### Connecting with a username and password

Below is a sample configuration that connects to NATS server with a username and password using a core NATS 
connection with NATS Streaming connection:

```
<nats.init>
    <url>nats://localhost:4222</url>
    <useCoreNatsConnection>true</useCoreNatsConnection>
    <natsUrl>nats://localhost:4223</natsUrl>
    <username>test</username>
    <password>test123</password>
</nats.init>
```
