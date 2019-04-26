package it.bz.idm.bdp.augeg4.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;

import it.bz.idm.bdp.augeg4.dto.fromauge.RawResVal;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.LinearResVal;
import org.junit.Ignore;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DataToJsonTest {

    @Test
    public void test_data_to_algorab() throws IOException, URISyntaxException {
        // given
        Date acq = new Date();
        acq.setTime(1555320011885l);

        AugeG4LinearizedDataDto augeG4LinearizedDataDto = new AugeG4LinearizedDataDto(
                "AIRQ01", acq,acq, Arrays.asList(
                new LinearResVal(101,4.1),
                new LinearResVal(102,3.7)
                        )
        );

        URL resource = DataFromJsonTest.class.getResource("/dataToAlgorab.json");
        String content = new String(Files.readAllBytes(Paths.get(resource.toURI())));

        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        mapper.setDateFormat(dateFormat);
        // when
        String json = mapper.writeValueAsString(Arrays.asList(augeG4LinearizedDataDto));

        // then
        assertEquals(content,json);
    }
}
