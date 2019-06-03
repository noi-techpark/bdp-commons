package it.bz.idm.bdp.augeg4.integration;

import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ProcessedDataToHubDto;
import it.bz.idm.bdp.augeg4.face.DataPusherHubFace;
import it.bz.idm.bdp.augeg4.mock.DataConverterHubMock;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Requires real Data Hub.
 */
@ContextConfiguration(locations = {"classpath:/META-INF/spring/applicationContext.xml"})
public class DataPusherIOT extends AbstractJUnit4SpringContextTests {

    @Autowired
    private DataPusherHubFace dataPusher;

    @Test
    public void test_sync_stations() {
        // given
        StationList list = getStationList();

        // when
        dataPusher.syncStations(list);

        // then no exception is thrown
    }

    private StationList getStationList() {
        StationList list = new StationList();
        StationDto station = new StationDto();
        station.setId("AUGEG4_STATION A");
        station.setName("non-unique name for station");
        station.setStationType("EnvironmentStation");
        station.setOrigin("origin"); // The source of our data set
        list.add(station);
        return list;
    }

    @Test
    public void test_sync_DataTypes() {
        // given
        DataTypeDto type = new DataTypeDto();
        type.setName("temperature");
        type.setPeriod(600);
        type.setUnit("°C");
        List<DataTypeDto> types = Collections.singletonList(type);

        // when
        dataPusher.syncDataTypes(types);

        // then no exception is thrown
    }

    @Test
    public void test_push_data() {
        // given
        StationList list = getStationList();
        dataPusher.syncStations(list);
        List<AugeG4ProcessedDataToHubDto> mockedData = new DataConverterHubMock().convert(new ArrayList<>());


        // when
        dataPusher.mapData(mockedData);
        dataPusher.pushData();

        // then no exception is thrown
    }
}
