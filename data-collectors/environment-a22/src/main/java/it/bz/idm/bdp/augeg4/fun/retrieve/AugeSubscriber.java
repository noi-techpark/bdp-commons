package it.bz.idm.bdp.augeg4.fun.retrieve;

import it.bz.idm.bdp.augeg4.ConnectorConfig;
import it.bz.idm.bdp.augeg4.util.AugeMqttClient;
import it.bz.idm.bdp.augeg4.util.AugeMqttConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class AugeSubscriber {

    private static final Logger LOG = LogManager.getLogger(AugeSubscriber.class.getName());

    MqttClient client;

    public static void main(String[] args) {
        new AugeSubscriber().listen(
            AugeMqttConfiguration.buildMqttSubscriberConfiguration(new ConnectorConfig()));
    }

    public AugeCallback listen(AugeMqttConfiguration mqttConfiguration) {
        AugeCallback callback = null;
        try {
            callback = new AugeCallback();
            client = AugeMqttClient.build(mqttConfiguration, callback);
            if (client.isConnected()) {
                LOG.debug("subscribe...:");
                /*
                Quality:
                    At most once (0)
                    At least once (1)
                    Exactly once (2).
                 */
                int quality = 1;
                client.subscribe(mqttConfiguration.getTopic(), quality);
                LOG.debug("listening...");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return callback;
    } 

    public void stop() {
        if (client!=null) {
            try {
                client.disconnectForcibly();
                client.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
