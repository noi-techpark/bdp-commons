package it.bz.idm.bdp.augeg4.fun.push;

import it.bz.idm.bdp.augeg4.ConnectorConfig;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4ProcessedDataToAugeDto;
import it.bz.idm.bdp.augeg4.dto.toauge.ProcessedResValToAuge;
import it.bz.idm.bdp.augeg4.util.AugeMqttConfiguration;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DataPusherAugeTest {


    class DataPusherAugeStub extends DataPusherAuge {

        public DataPusherAugeStub(AugeMqttConfiguration augeMqttConfiguration) {
            super(augeMqttConfiguration);
        }

        @Override
        protected void publish(String topic, String content, int qos, MqttClient client) throws MqttException {
            System.out.println(topic);
            System.out.println(content);
        }
    }

    @Test
    public void test_pushing_output_string() {
        DataPusherAugeStub stub = new DataPusherAugeStub(AugeMqttConfiguration.buildMqttPublisherConfiguration(new ConnectorConfig()));
        stub.pushData(mockedDataToSend());
    }

    private List<AugeG4ProcessedDataToAugeDto> mockedDataToSend() {
        // given
        Date acq = new Date();
        acq.setTime(1555320011885l);

        AugeG4ProcessedDataToAugeDto augeG4ProcessedDataToAugeDto = new AugeG4ProcessedDataToAugeDto(
                acq, acq, "AIRQ01", Arrays.asList(
                new ProcessedResValToAuge(101, 4.1),
                new ProcessedResValToAuge(102, 3.7)
        ));
        AugeG4ProcessedDataToAugeDto augeG4ProcessedDataToAugeDto2 = new AugeG4ProcessedDataToAugeDto(
                acq, acq, "AIRQ01", Arrays.asList(
                new ProcessedResValToAuge(101, 1.1),
                new ProcessedResValToAuge(102, 2.7)
        ));

        return Arrays.asList(augeG4ProcessedDataToAugeDto,augeG4ProcessedDataToAugeDto2);
    }

}
