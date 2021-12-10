package it.bz.idm.bdp.augeg4.util;

import it.bz.idm.bdp.augeg4.ConnectorConfig;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AugeMqttConfigurationIT {

    @Test
    public void test_mqtt_configurazion() {
        ConnectorConfig conConfig = new ConnectorConfig();
        AugeMqttConfiguration config = AugeMqttConfiguration.buildMqttPublisherConfiguration(conConfig);
        assertEquals("AirQuino/LinearizedData",config.getTopic());
        assertEquals("airQuinoLinearization",config.getClientID());
        assertEquals("61619",config.getServerPort());
        assertEquals("tcp://mqtt-test.algorab.net",config.getServerURI());
        assertEquals("testMQTT",config.getUserName());
        assertEquals("testMQTT",config.getUserPass());
        assertEquals("tcp://mqtt-test.algorab.net:61619",config.getServerURI() + ":" + config.getServerPort());
        try {
            AugeMqttClient.build(config);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
