# EI NATS Connector
[NATS](nats.io) is a distributed messaging platform based on the publish-subscribe messaging model
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
and paste it in the <EI_HOME>/lib folder and start using the connector.

# Building From the Source
Follow the steps given below to build the NATS connector from the source code:

1. Get a clone or download the source from the above repository.
2. Run the following Maven command from the esb-connector-nats directory: mvn clean install.
3. The NATS connector zip file is created in the esb-connector-nats/target directory.