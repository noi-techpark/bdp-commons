// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.dto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4ElaboratedDataDto;
import it.bz.idm.bdp.augeg4.dto.fromauge.ElaboratedResVal;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class DataFromJsonTest {

    @Test
    public void test_data_from_algorab() throws IOException, URISyntaxException, ParseException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));

        AugeG4ElaboratedDataDto augeG4ElaboratedDataDto = new AugeG4ElaboratedDataDto();
        augeG4ElaboratedDataDto.setControlUnitId("AIRQ01");
        Date acq = dateFormat.parse("2019-05-03T18:18:04");
        augeG4ElaboratedDataDto.setDateTimeAcquisition(acq);
        augeG4ElaboratedDataDto.setResVal(Arrays.asList(
                new ElaboratedResVal(1,339.0),
                new ElaboratedResVal(2,30.3,1,0.1,0.0),
                new ElaboratedResVal(3,51.4,1,0.1,0.0),
                new ElaboratedResVal(7,44.8,1,0.01,0.0),
                new ElaboratedResVal(8,12.0),
                new ElaboratedResVal(9,9.0),
                new ElaboratedResVal(10,125.0),
                new ElaboratedResVal(11,322.0),
                new ElaboratedResVal(12,11.0),
                new ElaboratedResVal(13,13.0),
                new ElaboratedResVal(14,93.0),
                new ElaboratedResVal(15,98.0),
                new ElaboratedResVal(16,2009.8,1,4.89,0.0)
                )
        );

        URL resource = DataFromJsonTest.class.getResource("/dataFromAlgorab.json");
        String content = new String(Files.readAllBytes(Paths.get(resource.toURI())));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // when
        String json = mapper.writeValueAsString(augeG4ElaboratedDataDto);
        AugeG4ElaboratedDataDto augeG4ElaboratedDataDtoReceived = mapper.readValue(content, AugeG4ElaboratedDataDto.class);

        // then
        assertEquals(content,json);
        assertEquals(augeG4ElaboratedDataDto, augeG4ElaboratedDataDtoReceived);
    }
}
