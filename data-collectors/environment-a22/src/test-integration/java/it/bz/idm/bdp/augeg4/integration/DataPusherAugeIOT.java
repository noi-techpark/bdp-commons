package it.bz.idm.bdp.augeg4.integration;

import it.bz.idm.bdp.augeg4.ConnectorConfig;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4ProcessedDataToAugeDto;
import it.bz.idm.bdp.augeg4.dto.toauge.ProcessedResValToAuge;
import it.bz.idm.bdp.augeg4.fun.push.DataPusherAuge;
import it.bz.idm.bdp.augeg4.util.AugeMqttConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Requires real Auge MQTT.
 */
public class DataPusherAugeIOT {

    @Test
    public void test_push_data() {
        try {
        // given
        List<AugeG4ProcessedDataToAugeDto> content = mockedDataToSend();
        DataPusherAuge dataPusherAuge = new DataPusherAuge(AugeMqttConfiguration.buildMqttPublisherConfiguration(new ConnectorConfig()));
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
