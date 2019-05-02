package it.bz.idm.bdp.augeg4.fun.push;

import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.face.DataPusherMapperFace;
import it.bz.idm.bdp.augeg4.mock.DataConverterMock;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.bz.idm.bdp.augeg4.mock.DataConverterMock.MOCKED_VALUE_PROCESSED;
import static it.bz.idm.bdp.augeg4.mock.DataConverterMock.MOCKED_VALUE_RAW;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataPusherMappingTest {

    private static final int MOCKED_PERIOD = 600;

    private final DataPusherMapperFace mapper = new DataPusherMapper(MOCKED_PERIOD);

    @Test
    public void test_mapping_from_AugeG4ToHubData_to_DataMapDto () {
        // given
        List<AugeG4ToHubDataDto> mockedData = new DataConverterMock().convert(new ArrayList<>());

        // when
        DataMapDto<RecordDtoImpl> rootMap = mapper.mapData(mockedData);

        // then
        DataMapDto<RecordDtoImpl> stationMap = rootMap.getBranch().get("STATION A");
        assertNotNull(stationMap);

        assertMeasurementEquals(stationMap, "temperature_raw", MOCKED_VALUE_RAW);
        assertMeasurementEquals(stationMap, "temperature_processed", MOCKED_VALUE_PROCESSED);
    }

    private void assertMeasurementEquals(DataMapDto<RecordDtoImpl> stationMap, String dataType, double value) {
        DataMapDto<RecordDtoImpl> parametersMap = stationMap.upsertBranch(dataType);
        assertNotNull(parametersMap);

        List<RecordDtoImpl> values = parametersMap.getData();
        assertEquals(values.size(), 1);

        SimpleRecordDto record = (SimpleRecordDto) values.get(0);
        assertEquals(record.getPeriod(), Integer.valueOf(MOCKED_PERIOD));
        assertEquals(record.getValue(), value);
    }


    @Test
    public void test_mapping_of_DataTypeDto () {
        // given
        DataTypeDto dataTypeDto = new DataTypeDto("temperature", "°C", "description", "Mean");

        // when
        List<DataTypeDto> mapped = mapper.mapDataTypes(Collections.singletonList(dataTypeDto));

        // then
        assertEquals(2, mapped.size());

        DataTypeDto raw = mapped.get(0);
        assertEquals("temperature_raw", raw.getName());
        assertNull(raw.getUnit());

        DataTypeDto processed = mapped.get(1);
        assertEquals("temperature_processed", processed.getName());
        assertEquals("°C", processed.getUnit());
    }

}
