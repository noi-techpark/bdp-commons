package it.bz.idm.bdp.augeg4.fun.retrieve;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4ElaboratedDataDto;
import it.bz.idm.bdp.augeg4.util.FixedQueue;

public class AugeCallback implements MqttCallback {

    private static final Logger LOG = LogManager.getLogger(AugeCallback.class.getName());

    private ObjectMapper mapper = new ObjectMapper();
    private FixedQueue<AugeG4ElaboratedDataDto> buffer;

    public  AugeCallback() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.buffer = new FixedQueue<>(100000);
    }

    @Override
    public void connectionLost(Throwable cause) {
        LOG.debug("Connection to MQTT broker lost!");
        LOG.debug(cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        LOG.debug("Message received:\n\t"+ new String(message.getPayload()) );
        AugeG4ElaboratedDataDto augeG4ElaboratedDataDtoReceived = mapper.readValue(message.getPayload(), AugeG4ElaboratedDataDto.class);
        buffer.add(augeG4ElaboratedDataDtoReceived);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOG.debug("Delivery complete!");
    }

    public List<AugeG4ElaboratedDataDto> fetchData() {
        List<AugeG4ElaboratedDataDto> fetchedAugeG4FromAlgorabDataDto = new ArrayList<>();
        buffer.drainTo(fetchedAugeG4FromAlgorabDataDto);
        return fetchedAugeG4FromAlgorabDataDto;
    }
}
