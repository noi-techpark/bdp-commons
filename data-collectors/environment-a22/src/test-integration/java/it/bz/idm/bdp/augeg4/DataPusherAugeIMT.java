package it.bz.idm.bdp.augeg4;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4ProcessedDataToAugeDto;
import it.bz.idm.bdp.augeg4.dto.toauge.ProcessedResValToAuge;
import it.bz.idm.bdp.augeg4.fun.push.DataPusherAuge;
import it.bz.idm.bdp.augeg4.util.AugeMqttConfiguration;

/**
 * Requires real Auge MQTT.
 */
public class DataPusherAugeIMT {

    @Test
    public void test_push_data() {
        try {
        // given
        List<AugeG4ProcessedDataToAugeDto> content = mockedDataToSend();
            ConnectorConfig config = new ConnectorConfig();
            config.mqtt_unit_test=true;
            DataPusherAuge dataPusherAuge = new DataPusherAuge(AugeMqttConfiguration.buildMqttPublisherConfiguration(config));
        // when
        dataPusherAuge.pushData(content);
    } catch (Exception e) {
        e.printStackTrace();
        Assert.fail();
    }

        // then
        // no exception thrown
        System.exit(0);
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

        return Arrays.asList(augeG4ProcessedDataToAugeDto);
    }
}
