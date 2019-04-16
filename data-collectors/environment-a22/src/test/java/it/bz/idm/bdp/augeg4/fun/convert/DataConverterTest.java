package it.bz.idm.bdp.augeg4.fun.convert;

import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.LinearResVal;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.Measurement;
import it.bz.idm.bdp.augeg4.face.DataConverterFace;
import it.bz.idm.bdp.augeg4.fun.convert.DataConverter;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataConverterTest {

    private final DataConverterFace converter = new DataConverter("prefix-");

    @Test
    public void convert_AugeG4LinearizedData_to_AugeG4ToHubData () {
        // given
        LinearResVal resource = new LinearResVal(101, 1);
        List<LinearResVal> resources = Collections.singletonList(resource);
        Date acquisition = new Date();
        Date linearization = new Date();
        String controlUnitId = "STATION A";
        AugeG4LinearizedDataDto linearizedDto = new AugeG4LinearizedDataDto(controlUnitId, acquisition, linearization, resources);
        List<AugeG4LinearizedDataDto> linearizedDtos = Collections.singletonList(linearizedDto);

        // when
        List<AugeG4ToHubDataDto> list = converter.convert(linearizedDtos);

        // then
        assertEquals(list.size(), linearizedDtos.size());

        AugeG4ToHubDataDto toHubDto = list.get(0);
        assertEquals("prefix-STATION A", toHubDto.getStation());
        assertEquals(linearizedDto.getDateTimeAcquisition(), toHubDto.getAcquisition());

        List<Measurement> measurements = toHubDto.getMeasurements();
        assertEquals(resources.size(), measurements.size());

        Measurement measurement = measurements.get(0);
        assertEquals(Double.valueOf(resource.getValue()), Double.valueOf(measurement.getValue()));
        assertEquals("temperature", measurement.getDataType());
    }


}
