package it.bz.idm.bdp.dcemobilityh2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcemobilityh2.dto.HydrogenDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class HydrogenDataPusherIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(HydrogenDataPusherIT.class.getName());

    @Autowired
    private HydrogenJobScheduler scheduler;

    @Autowired
    private HydrogenDataPusher pusher;

    @Autowired
    private HydrogenDataRetriever reader;

    private boolean doPush = true;

    @Test
    public void testSchedulerPush() {
        if ( !doPush ) {
            return;
        }
        try {
            scheduler.pushStations();
            scheduler.pushData();
        } catch (Exception e) {
            LOG.error("Exception in testSchedulerPush: "+e, e);
            Assert.fail();
        }
    }

    @Test
    public void testPush() {
        if ( !doPush ) {
            return;
        }

        List<String> errors = new ArrayList<String>();
        List<HydrogenDto> data = null;

        try {
            String responseString = HydrogenDataRetrieverTest.getTestData(HydrogenDataRetrieverTest.DATA_PUSH);
            data = reader.convertResponseToInternalDTO(responseString);
        } catch (Exception e) {
            LOG.error("Exception in testPush: "+e, e);
            Assert.fail();
        }

        pushStations(data, errors);
        pushPlugs(data, errors);
        pushDataTypes(data, errors);
        pushStationData(data, errors);
        pushPlugData(data, errors);

        if ( errors.size() > 0 ) {
            for (String err : errors) {
                LOG.error(err);
            }
            Assert.fail();
        }
    }

    private void pushStations(List<HydrogenDto> data, List<String> errors) {
        try {
            StationList stations = pusher.mapStations2Bdp(data);
            LOG.debug(stations);
            if (stations != null) {
                pusher.syncStations(stations);
            }
        } catch (Exception e) {
            errors.add("STATIONS: "+e);
        }
    }

    private void pushPlugs(List<HydrogenDto> data, List<String> errors) {
        try {
            StationList plugs    = pusher.mapPlugs2Bdp(data);
            StationList tmp = new StationList();
            for (StationDto stationDto : plugs) {
                LOG.debug(stationDto);
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

    private void pushDataTypes(List<HydrogenDto> data, List<String> errors) {

        try {
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);
            DataMapDto<RecordDtoImpl> plugRec    = pusher.mapPlugData2Bdp(data);
            DataMapDto<RecordDtoImpl> allRec = new DataMapDto<RecordDtoImpl>();

            //Take data from stations and plugs
            if (stationRec != null) {
                Map<String, DataMapDto<RecordDtoImpl>> branch = stationRec.getBranch();
                Set<String> keySet = branch.keySet();
                for (String key : keySet) {
                    allRec.getBranch().put(key, branch.get(key));
                }
            }
            if (plugRec != null) {
                Map<String, DataMapDto<RecordDtoImpl>> branch = plugRec.getBranch();
                Set<String> keySet = branch.keySet();
                for (String key : keySet) {
                    allRec.getBranch().put(key, branch.get(key));
                }
            }

            //Extract DataTypes from station data and plug data
            if (allRec != null) {
                List<DataTypeDto> dataTypeList = new ArrayList<DataTypeDto>();
                Set<String> dataTypeNames = new HashSet<String>();

                Map<String, DataMapDto<RecordDtoImpl>> branch1 = allRec.getBranch();
                Set<String> keySet1 = branch1.keySet();
                for (String key1 : keySet1) {
                    LOG.debug("check key1="+key1);
                    DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(key1);

                    Map<String, DataMapDto<RecordDtoImpl>> branch2 = dataMapDto1.getBranch();
                    Set<String> keySet2 = branch2.keySet();
                    for (String key2 : keySet2) {
                        LOG.debug("check key2="+key2);
                        DataMapDto<RecordDtoImpl> dataMapDto2 = branch2.get(key2);
                        List<RecordDtoImpl> data2 = dataMapDto2.getData();

                        for (RecordDtoImpl recordDtoImpl : data2) {
                            LOG.debug("check recordDtoImpl="+recordDtoImpl);
                            SimpleRecordDto sr = (SimpleRecordDto) recordDtoImpl;

                            if ( !dataTypeNames.contains(key2) ) {
                                DataTypeDto type = new DataTypeDto();
                                type.setName(key2);
                                type.setPeriod(sr.getPeriod());
//                              type.setUnit(dto.getUnit());
                                LOG.debug("ADD DataTypeDto="+type);
                                dataTypeList.add(type);
                                dataTypeNames.add(key2);
                            }
                        }

                    }

                }

                //Push DataTypes
                pusher.syncDataTypes(dataTypeList);

            }
        } catch (Exception e) {
            errors.add("DATA-TYPE-REC: "+e);
        }

    }

    private void pushStationData(List<HydrogenDto> data, List<String> errors) {
        try {
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);
            if (stationRec != null) {
                pusher.pushData(stationRec);
            }
        } catch (Exception e) {
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
            errors.add("PLUG-REC: "+e);
        }
    }

}
