// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4ProcessedDataToAugeDto;
import it.bz.idm.bdp.augeg4.dto.toauge.ProcessedResValToAuge;
import org.junit.Test;

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

import static junit.framework.TestCase.assertEquals;

public class DataToJsonTest {

    @Test
    public void test_data_to_algorab() throws IOException, URISyntaxException {
        // given
        Date acq = new Date();
        acq.setTime(1555320011885l);

        AugeG4ProcessedDataToAugeDto augeG4ProcessedDataToAugeDto = new AugeG4ProcessedDataToAugeDto(
                acq, acq, "AIRQ01", Arrays.asList(
                new ProcessedResValToAuge(101, 4.1),
                new ProcessedResValToAuge(102, 3.7)
        ));

        URL resource = DataFromJsonTest.class.getResource("/dataToAlgorab.json");
        String content = new String(Files.readAllBytes(Paths.get(resource.toURI())));

        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        mapper.setDateFormat(dateFormat);
        // when
        String json = mapper.writeValueAsString(Arrays.asList(augeG4ProcessedDataToAugeDto));

        // then
        assertEquals(content, json);
    }
}
