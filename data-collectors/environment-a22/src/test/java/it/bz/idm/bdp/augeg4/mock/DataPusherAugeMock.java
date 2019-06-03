package it.bz.idm.bdp.augeg4.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4ProcessedDataToAugeDto;
import it.bz.idm.bdp.augeg4.face.DataPusherAugeFace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DataPusherAugeMock implements DataPusherAugeFace {

    private static final Logger LOG = LogManager.getLogger(DataPusherAugeMock.class.getName());
    ObjectMapper mapper = new ObjectMapper();


    @Override
    public void pushData(List<AugeG4ProcessedDataToAugeDto> dataToAuge) {
        LOG.info("pushDataToAuge()");
        log(dataToAuge);
    }


    private void log(Object objToLog) {
        System.out.println(toJson(objToLog));
    }


    private String toJson(Object objToJson) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objToJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "toJsonFail";
    }

}
