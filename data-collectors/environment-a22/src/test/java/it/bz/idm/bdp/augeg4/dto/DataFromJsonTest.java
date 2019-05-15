package it.bz.idm.bdp.augeg4.dto;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import it.bz.idm.bdp.augeg4.dto.fromauge.RawResVal;

public class DataFromJsonTest {

    @Test
    public void test_data_from_algorab() throws IOException, URISyntaxException {
        AugeG4FromAlgorabDataDto augeG4FromAlgorabDataDto = new AugeG4FromAlgorabDataDto();
        augeG4FromAlgorabDataDto.setControlUnitId("AIRQ01");
        Date acq = new Date();
        acq.setTime(1555320011885l);
        augeG4FromAlgorabDataDto.setDateTimeAcquisition(acq);
        augeG4FromAlgorabDataDto.setResVal(Arrays.asList(
                new RawResVal(1,2.3,1,0.0,1.0),
                new RawResVal(2,2.3,3,0.1,2.0)
                )
        );

        URL resource = DataFromJsonTest.class.getResource("/dataFromAlgorab.json");
        String content = new String(Files.readAllBytes(Paths.get(resource.toURI())));

        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        mapper.setDateFormat(dateFormat);

        // when
        String json = mapper.writeValueAsString(Arrays.asList(augeG4FromAlgorabDataDto));

        // then
        assertEquals(content,json);
    }
}
