# EI NATS Connector
[NATS](https://nats.io/) is a distributed messaging platform based on the publish-subscribe messaging model
where publishers publish messages on a paticular subject and consumers listening on that 
subject can consume these messages. For more information, see the [NATS documentation.](https://nats-io.github.io/docs/)

The NATS connector allows you to access the NATS API through WSO2 EI and acts as a message 
publisher that facilitates message publishing. 
The NATS connector sends messages to NATS brokers.

# Getting started 
__Download and install the connector__

1. Download the connector from the [WSO2 Store](https://store.wso2.com/store/assets/esbconnector/details/3fcaf309-1a69-4edf-870a-882bb76fdaa1) 
by clicking the Download Connector button.

2. You can then follow this [documentation](https://docs.wso2.com/display/EI650/Working+with+Connectors+via+the+Management+Console) to add the connector to your WSO2 EI instance 
and to enable it (via the management console).

3. For more information on using connectors and their operations in your WSO2 EI configurations, see [Using a Connector](https://docs.wso2.com/display/EI650/Using+a+Connector).

4. If you want to work with connectors via WSO2 EI Tooling, see [Working with Connectors via Tooling](https://docs.wso2.com/display/EI650/Working+with+Connectors+via+Tooling).

# Configuring the connector operations
Install NATS Server from the [NATS](https://nats.io/download/) website download page. Download and install the latest NATS jar file from [this](https://mvnrepository.com/artifact/io.nats/jnats) website 
and paste it in the <EI_HOME>/lib folder. To use the NATS connector, add the <Nats.init> element to your configuration. Given 
below are sample scenarios of using the NATS connector.

__Creating a publisher with security__

Below is a sample configuration that creates a publisher with security:
```
<Nats.init>
    <servers>nats://localhost:4222</servers>
    <tlsKeyStoreLocation><PATH_TO_KEYSTORE_FILE></tlsKeyStoreLocation>
    <tlsKeyStorePassword>password</tlsKeyStorePassword>
    <tlsTrustStoreLocation><PATH_TO_TRUSTSTORE_FILE></tlsTrustStoreLocation>
    <tlsTrustStorePassword>password</tlsTrustStorePassword>
</Nats.init>
```
If you want to create a publisher without security, simply provide the ```<servers>``` parameter excluding the other parameters. If the <servers> parameter is
not provided, the connector will automatically connect to nats://localhost:4222. You can connect to multiple 
servers by providing a comma separated list in the ```<servers>``` parameter, for example:

```
<Nats.init>
    <servers>nats://localhost:4222, nats://localhost:4223</servers>
</Nats.init>
```

__Connecting with a username and password__

Below is a sample configuration that connects to NATS with a username and password:

```
<Nats.init>
    <servers>nats://localhost:4222</servers>
    <username>test</username>
    <password>test123</password>
</Nats.init>
```
__Creating a connection pool__

Below is a sample configuration that creates a connection pool of 10 connections:

```
<Nats.init>
    <servers>nats://localhost:4222</servers>
    <maxPoolSize>10</maxPoolSize>
</Nats.init>
```
If no value is provided, then a default value of 5 will be used.

The connection can be configured with multiple parameters of which a list with descriptions can be found in the init.xml
file in the src/main/resources/nats_config directory of this repository.

After configuring the connection in the <Nats.init> element, you can use the connector to send messages. Below are sample
scenarios for sending messages to NATS server.

__Sending a message__

Below is a sample configuration that can be used to send a message to NATS server on a subject and receive a response from the consumer:

```
<Nats.sendMessage>
    <subject>test</subject>
    <getResponse>true</getResponse>
</Nats.sendMessage>
```
Omit the ```<getResponse>``` parameter if you do not want a response (fire-and-forget). 

__Sending a message with headers__

Below is a sample configuration that can be used to send a message to NATS server along with message headers:

```
<Nats.sendMessage>
    <subject>test</subject>
    <getResponse>true</getResponse>
    <test.Content-Type>application/json</test.Content-Type>
</Nats.sendMessage>
```
You can provide any number of headers, but the format of the parameter is <SUBJECT_NAME.HEADER>.

__Sending a message to multiple subjects__

Below is a sample configuration that can be used to send a message to NATS server on multiple subjects:

```
<Nats.sendMessage>
    <subject>test1</subject>
    <getResponse>true</getResponse>
</Nats.sendMessage>
<Nats.sendMessage>
    <subject>test2</subject>
    <getResponse>true</getResponse>
</Nats.sendMessage>
```

# Building From the Source
Follow the steps given below to build the NATS connector from the source code:

1. Get a clone or download the source from the above repository.
2. Run the following Maven command from the esb-connector-nats directory: mvn clean install.
3. The NATS connector zip file is created in the esb-connector-nats/target directory.