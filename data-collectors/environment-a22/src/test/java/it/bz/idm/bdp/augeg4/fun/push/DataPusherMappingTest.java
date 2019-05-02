package it.bz.idm.bdp.augeg4.fun.push;

import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.face.DataPusherMapperFace;
import it.bz.idm.bdp.augeg4.mock.DataConverterMock;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class DataPusherMappingTest {

    private static final int MOCKED_PERIOD = 600;

    private final DataPusherMapperFace mapper = new DataPusherMapper(MOCKED_PERIOD);

    @Test
    public void test_mapping_from_AugeG4ToHubData_to_DataMapDto () {
        // given
        List<AugeG4ToHubDataDto> mockedData = new DataConverterMock().convert(new ArrayList<>());

        // when
        DataMapDto<RecordDtoImpl> map = mapper.map(mockedData);

        // then
        DataMapDto<RecordDtoImpl> stationMap = map.getBranch().get("STATION A");
        assertNotNull(stationMap);

        DataMapDto<RecordDtoImpl> parametersMap = stationMap.upsertBranch("temperature");
        assertNotNull(parametersMap);

        List<RecordDtoImpl> values = parametersMap.getData();
        assertEquals(values.size(), 1);

        SimpleRecordDto record = (SimpleRecordDto) values.get(0);
        assertEquals(Long.valueOf(record.getPeriod()), Long.valueOf(MOCKED_PERIOD));
    }

}
