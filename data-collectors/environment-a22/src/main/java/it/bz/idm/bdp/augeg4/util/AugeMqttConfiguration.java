package it.bz.idm.bdp.augeg4.util;

import it.bz.idm.bdp.augeg4.ConnectorConfig;

/**
 * MqttConfig
 */
public class AugeMqttConfiguration {

    private String userName;
    private String userPass;
    private String serverURI;
    private String serverPort;
    private String topic;
    private String clientID;

    public AugeMqttConfiguration() {
    }

    public static AugeMqttConfiguration buildMqttSubscriberConfiguration(ConnectorConfig config) {
        return new AugeMqttConfiguration()
                .setUserName(config.mqtt_subscriber_username)
                .setUserPass(config.mqtt_subscriber_password)
                .setServerURI(config.mqtt_subscriber_uri)
                .setServerPort(config.mqtt_subscriber_port)
                .setClientID(config.mqtt_subscriber_clientid)
                .setTopic(config.mqtt_subscriber_topic);
    }

    public static AugeMqttConfiguration buildMqttPublisherConfiguration(ConnectorConfig config) {
        return new AugeMqttConfiguration()
                .setUserName(config.mqtt_publisher_username)
                .setUserPass(config.mqtt_publisher_password)
                .setServerURI(config.mqtt_publisher_uri)
                .setServerPort(config.mqtt_publisher_port)
                .setClientID(config.mqtt_publisher_clientid)
                .setTopic(config.mqtt_publisher_topic);
    }


    /**
     * @param clientID the clientID to set
     */
    public AugeMqttConfiguration setClientID(String clientID) {
        this.clientID = clientID;
        return this;
    }

    /**
     * @param serverPort the serverPort to set
     */
    public AugeMqttConfiguration setServerPort(String serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    /**
     * @param topic the topic to set
     */
    public AugeMqttConfiguration setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    /**
     * @param serverURI the serverURI to set
     */
    public AugeMqttConfiguration setServerURI(String serverURI) {
        this.serverURI = serverURI;
        return this;
    }

    /**
     * @param userName the userName to set
     */
    public AugeMqttConfiguration setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * @param userPass the userPass to set
     */
    public AugeMqttConfiguration setUserPass(String userPass) {
        this.userPass = userPass;
        return this;
    }

    /**
     * @return the clientID
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * @return the serverPort
     */
    public String getServerPort() {
        return serverPort;
    }

    /**
     * @return the serverURI
     */
    public String getServerURI() {
        return serverURI;
    }

    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the userPass
     */
    public String getUserPass() {
        return userPass;
    }


}