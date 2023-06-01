// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.fun.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4ProcessedDataToAugeDto;
import it.bz.idm.bdp.augeg4.face.DataPusherAugeFace;
import it.bz.idm.bdp.augeg4.util.AugeMqttClient;
import it.bz.idm.bdp.augeg4.util.AugeMqttConfiguration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.v;


public class DataPusherAuge implements DataPusherAugeFace {

    private static final Logger LOG = LoggerFactory.getLogger(DataPusherAuge.class.getName());


    private AugeMqttConfiguration augeMqttConfiguration;

    private MqttClient client;

	private void logMqttException(MqttException ex) {
		LOG.error(
			"Mqtt auge connection error: {}", ex.getMessage(),
			v("reason", ex.getReasonCode()),
            v("msg", ex.getMessage()),
            v("loc", ex.getLocalizedMessage()),
            v("cause", ex.getCause()),
            v("excep", Arrays.asList(ex.getStackTrace()))
		);

		if (LOG.isDebugEnabled()) {
			ex.printStackTrace();
		}

	}

    public DataPusherAuge(AugeMqttConfiguration augeMqttConfiguration) {
        this.augeMqttConfiguration = augeMqttConfiguration;
        try {
            client = AugeMqttClient.build(augeMqttConfiguration);
            LOG.debug("Mqtt Auge connected.");
        } catch (MqttException e) {
			logMqttException(e);
        }
    }

    @Override
    public void pushData(List<AugeG4ProcessedDataToAugeDto> list) {
        int QUALITY_OF_SERVICE = 1;
        try {
            client = AugeMqttClient.build(augeMqttConfiguration);
			if (client == null || !client.isConnected()) {
				throw new MqttException(MqttException.REASON_CODE_CLIENT_NOT_CONNECTED);
			}
            LOG.info("Mqtt Auge connected");
			for(AugeG4ProcessedDataToAugeDto augeDto: list) {
				String content = jsonOf(augeDto);
				LOG.debug(content);
				publish(augeMqttConfiguration.getTopic(), content, QUALITY_OF_SERVICE, client);
			}
        } catch (MqttException e) {
            logMqttException(e);
        }
    }

    private String jsonOf(AugeG4ProcessedDataToAugeDto augeDto) {
        if (augeDto == null) {
            throw new IllegalArgumentException();
        }

        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        //dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        mapper.setDateFormat(dateFormat);
        String json = null;
        try {
            json = mapper.writeValueAsString(augeDto);
        } catch (JsonProcessingException e) {
            LOG.warn("JSON processing error: {}", e.getMessage());
        }
        return json;
    }

    protected void publish(String topic, String content, int qos, MqttClient client) throws MqttException {
        LOG.debug("Publishing message: "+content);
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        client.publish(topic, message);
        LOG.debug("topic ["+topic+"]");
        LOG.debug("message ["+message+"]");
        LOG.debug("Message published");
    }


}
