package it.bz.idm.bdp.augeg4.util;

import it.bz.idm.bdp.augeg4.fun.retrieve.AugeCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class AugeMqttClient {

    private static final Logger LOG = LogManager.getLogger(AugeMqttClient.class.getName());
    
    public static MqttClient build(AugeMqttConfiguration mqttConfiguration) throws MqttException {
        MqttClient client = buildMqttClient(mqttConfiguration);
        LOG.debug("connect...:");
        client.connect(buildConnOpts(mqttConfiguration));
        return client;
    }

    public static MqttClient build(AugeMqttConfiguration mqttConfiguration, AugeCallback callback) throws MqttException  {
        MqttClient client = buildMqttClient(mqttConfiguration);
        client.setCallback(callback);
        LOG.debug("connect...:");
        client.connect(buildConnOpts(mqttConfiguration));
        return client;
    }

    private static MqttClient buildMqttClient(AugeMqttConfiguration mqttConfiguration) throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        String serverPort = mqttConfiguration.getServerPort();
        String serverURL = mqttConfiguration.getServerURI() + ":" + serverPort;
        String clientID = mqttConfiguration.getClientID();
        String topic = mqttConfiguration.getTopic();
        LOG.debug("MQTT client with:" + serverURL + " " + clientID + " " + topic);
        return new MqttClient(serverURL, clientID, persistence);
    }

    private static MqttConnectOptions buildConnOpts(AugeMqttConfiguration mqttConfiguration) {
        String userName = mqttConfiguration.getUserName();
        String userPass = mqttConfiguration.getUserPass();
        LOG.debug("Connection with:" + userName + " " + userPass);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(false);
        connOpts.setPassword(userPass.toCharArray());
        connOpts.setUserName(userName);
        int msTimeout = 5 * 1000;
        connOpts.setConnectionTimeout(msTimeout);
        connOpts.setAutomaticReconnect(true);
        return connOpts;
    }
}
