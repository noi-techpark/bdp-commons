package it.bz.idm.bdp.augeg4.fun.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4ProcessedDataToAugeDto;
import it.bz.idm.bdp.augeg4.face.DataPusherAugeFace;
import it.bz.idm.bdp.augeg4.util.AugeMqttClient;
import it.bz.idm.bdp.augeg4.util.AugeMqttConfiguration;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;


public class DataPusherAuge implements DataPusherAugeFace {

    private AugeMqttConfiguration augeMqttConfiguration;

    public DataPusherAuge(AugeMqttConfiguration augeMqttConfiguration) {
        this.augeMqttConfiguration = augeMqttConfiguration;
    }

    @Override
    public void pushData(List<AugeG4ProcessedDataToAugeDto> list) {
        String topic        = augeMqttConfiguration.getTopic();
        int qos             = 1;

        String content = jsonOf(list);
        try {
            MqttClient client = AugeMqttClient.build(augeMqttConfiguration);
            if (client.isConnected()) {
                publish(topic, content, qos, client);
                client.disconnect();
            }
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

    private String jsonOf(List<AugeG4ProcessedDataToAugeDto> list) {
        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        mapper.setDateFormat(dateFormat);
        String json = null;
        try {
            json = mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void publish(String topic, String content, int qos, MqttClient client) throws MqttException {
        System.out.println("Publishing message: "+content);
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        client.publish(topic, message);
        System.out.println("topic ["+topic+"]");
        System.out.println("message ["+message+"]");
        System.out.println("Message published");
    }


}
