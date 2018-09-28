package it.bz.idm.bdp.dcemobilityh2;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcemobilityh2.HydrogenJobScheduler;
import it.bz.idm.bdp.dcemobilityh2.dto.HydrogenDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.emobility.EchargingStationDto;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class HydrogenDataPusherTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private HydrogenJobScheduler scheduler;

    @Autowired
    private HydrogenDataPusher pusher;

    @Autowired
    private HydrogenDataRetriever reader;

    private boolean doTests = false;

    @Test
    public void testSchedulerPush() {
        if ( !doTests ) {
            return;
        }
        try {
            scheduler.pushStations();
            scheduler.pushData();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testPush() {
        if ( !doTests ) {
            return;
        }

        List<String> errors = new ArrayList<String>();
        List<HydrogenDto> data = null;

        try {
            String responseString = HydrogenDataRetrieverTest.TEST_RESPONSE_STRING;
            data = reader.convertResponseToInternalDTO(responseString);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        pushStations(data, errors);
//        pushPlugs(data, errors);
//        pushStationData(data, errors);
//        pushPlugData(data, errors);

        if ( errors.size() > 0 ) {
            for (String err : errors) {
                System.out.println(err);
            }
            Assert.fail();
        }
    }

    private void pushStations(List<HydrogenDto> data, List<String> errors) {
        try {
            StationList stations = pusher.mapStations2Bdp(data);
            StationList tmp = new StationList();
            for (StationDto stationDto : stations) {
                System.out.println(stationDto);
                EchargingStationDto dto = (EchargingStationDto) stationDto;
//                stationDto.setStationType(null);
//                stationDto.setOrigin(null);
//                stationDto.setMunicipality(null);
//                stationDto.setName("CIAO");
                dto.setAccessInfo(null);
                dto.setPaymentInfo(null);
                if ( tmp.size()==0 ) {
                    tmp.add(stationDto);
                }
            }
            stations = tmp;
            System.out.println(stations);
            if (stations != null) {
                pusher.syncStations(stations);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            errors.add("STATIONS: "+e);
        }
    }

    private void pushPlugs(List<HydrogenDto> data, List<String> errors) {
        try {
            StationList plugs    = pusher.mapPlugs2Bdp(data);
            StationList tmp = new StationList();
            for (StationDto stationDto : plugs) {
                System.out.println(stationDto);
                stationDto.setStationType(null);
                if ( tmp.size()==0 ) {
                    tmp.add(stationDto);
                }
            }
            plugs = tmp;
            if (plugs != null) {
                pusher.syncStations("EChargingPlug", plugs);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            errors.add("PLUGS: "+e);
        }
    }

    private void pushStationData(List<HydrogenDto> data, List<String> errors) {
        try {
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);
            if (stationRec != null) {
                pusher.pushData(stationRec);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("STATION-REC: "+e);
        }
    }

    private void pushPlugData(List<HydrogenDto> data, List<String> errors) {
        try {
            DataMapDto<RecordDtoImpl> plugRec = pusher.mapPlugData2Bdp(data);
            if (plugRec != null){
                pusher.pushData("EChargingPlug",plugRec);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("PLUG-REC: "+e);
        }
    }

}
