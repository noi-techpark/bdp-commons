// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import info.datatellers.appabz.DataFetcher;
import info.datatellers.appabz.DataPusher;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

public class DataPusherIT {

    private DataPusher pusher = new DataPusher();
    private static final Logger LOG = LoggerFactory.getLogger(DataPusherIT.class.getName());
    private final ResourceBundle rb = ResourceBundle.getBundle("test");

    @Test
    public void mapStationsTest()
    {
        LOG.info("mapStationsTest execution started.");
        StationList testStationList = pusher.mapStations(true);
        for (StationDto station : testStationList) {
            try{
                //Check stations have no null field
                Assert.assertNotNull(station.getId());
                Assert.assertNotNull(station.getName());
                Assert.assertNotNull(station.getOrigin());
                Assert.assertNotNull(station.getCoordinateReferenceSystem());
                Assert.assertNotNull(station.getMetaData().get("municipality"));
                Assert.assertNotNull(station.getStationType());
                Assert.assertNotNull(station.getLatitude());
                Assert.assertNotNull(station.getLongitude());

                //Checking stations ids contains appabz
                Assert.assertTrue(station.getId().contains("APPABZ_"));
            }catch (AssertionError e)
            {
                LOG.debug(e.getMessage() + " for station " + station.getId() +  " " + station.getName());
            }
        }
        LOG.info("mapStationsTest execution terminated.");
    }

    @Test
    public void mapTypesTest()
    {
        LOG.info("mapTypesTest execution started.");
        HashMap<String, DataTypeDto> testTypesMap = pusher.mapTypes(true);
        for (int looper = 0; looper < testTypesMap.keySet().size(); looper++)
        {
            DataTypeDto polluter = testTypesMap.get(testTypesMap.keySet().toArray()[looper]);
            try{
                Assert.assertNotNull(polluter.getName());
                Assert.assertEquals(3600, (int) polluter.getPeriod());
                Assert.assertNotNull(polluter.getRtype());
                Assert.assertTrue(polluter.getUnit().equals(rb.getString("odh.test.polluters.micro"))
                        || polluter.getUnit().equals(rb.getString("odh.test.polluters.milli"))
                        || polluter.getUnit().equals(rb.getString("odh.test.polluters.radio")));
            }catch(AssertionError e)
            {
                LOG.debug(e.getMessage() + " for station " + polluter.getName());
            }
        }
        LOG.info("mapTypesTest execution terminated.");
    }

    @Test
    public void mapDataTest()
    {
        DataMapDto<RecordDtoImpl> rootMap = constructRootMap();
        DataPusher dataPusher = new DataPusher();
        ArrayList<String> stationIDs = new ArrayList<>();
        ArrayList<String> pollutersIDs = new ArrayList<>();

        for (int index = 0; index < rootMap.getBranch().keySet().size(); index++)
        {
            String stationID = rootMap.getBranch().keySet().toArray()[index].toString();
            stationIDs.add(stationID);

            for (int looper = 0; looper < rootMap.getBranch().get(stationID).getBranch().keySet().size(); looper++)
            {
                String polluter = rootMap.getBranch().get(stationID).getBranch().keySet().toArray()[looper].toString();
                if (!pollutersIDs.contains(polluter))
                {
                    pollutersIDs.add(polluter);
                }
            }
        }

        dataPusher.mapData(rootMap, true);

        for (int looper = 0; looper < rootMap.getBranch().keySet().size(); looper++)
        {
            Assert.assertNotNull(stationIDs.get(looper));

            for (int index = 0; index < rootMap.getBranch().get(stationIDs.get(looper)).getBranch().keySet().size(); index++) {
                String polluter = pollutersIDs.get(index);
                try {
                    if ((rootMap.getBranch().get(stationIDs.get(looper)).getBranch().get(polluter).getData().size() != 0)) {
                        Assert.assertNotNull(rootMap.getBranch().get(stationIDs.get(looper)).getBranch()
                            .get(polluter).getData());
                    } else {
                        LOG.info("Unexpected outcome: on date " + new DataFetcher().getDateOfRecord() + " no data was collected for polluter "
                            + polluter + " at station " + rootMap.getBranch().keySet().toArray()[looper].toString() + ".");
                    }
                }catch (NullPointerException e) {
                    LOG.info("Unexpected outcome: on date " + new DataFetcher().getDateOfRecord() + " no data was collected for polluter "
                            + polluter + " at station " + rootMap.getBranch().keySet().toArray()[looper].toString() + ". No such "
                        + "polluter branch exists.");
                }
            }
        }
    }

    private static DataMapDto<RecordDtoImpl> constructRootMap() {
        LOG.info("Starting to construct rootMap.");
        DataPusher pusher = new DataPusher();
        StationList stationList = pusher.mapStations(true);
        String[] pollutersName = pusher.mapTypes(true).keySet().toArray(new String[0]);

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        for (StationDto station : stationList) {
            DataMapDto<RecordDtoImpl> stationMap = map.upsertBranch(station.getId());
            stationMap.setName(station.getName());
            LOG.debug("First deep branch created.");
            for (String polluterName : pollutersName) {
                String key = polluterName.trim().replace("\"", "");
                DataMapDto<RecordDtoImpl> measurementMap = stationMap.upsertBranch(key.trim());
                measurementMap.setName(polluterName.trim().replace("\"", ""));
                LOG.debug("Second deep branch created.");
            }
        }
        LOG.info("Map constructed.");
        return map;
    }
}
