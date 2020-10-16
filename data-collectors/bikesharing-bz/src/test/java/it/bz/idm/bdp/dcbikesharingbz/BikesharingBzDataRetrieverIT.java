package it.bz.idm.bdp.dcbikesharingbz;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzDto;
import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzStationDto;
import it.bz.idm.bdp.dto.DataTypeDto;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class BikesharingBzDataRetrieverIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(BikesharingBzDataRetrieverIT.class.getName());

    @Autowired
    private BikesharingBzDataRetriever reader;

    @Autowired
    private BikesharingBzDataPusher pusher;

    @Test
    public void testFetchData() {
        try {
            //Fetch data from source origin
            BikesharingBzDto bzDto = reader.fetchData();
            List<BikesharingBzStationDto> fetchData = bzDto.getStationList();

            //Check there is at least one item in the list
            assertNotNull("Fetched data IS NULL", fetchData);
            if ( fetchData.size() == 0 ) {
                Assert.fail("Fetched data IS EMPTY");
            }

            //Log all formatted data, only if DEBUG is enabled
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(
                        DCUtils.rpad("Bike",13,' ')+"  "+DCUtils.rpad("Parking Name",30,' ')+
                        "  "+DCUtils.rpad("Latitude",17,' ')+"  "+DCUtils.rpad("Longitude",17,' ')+"  "+DCUtils.rpad("Loc Kind",15,' ')+
                        "  "+DCUtils.rpad("Mainten",7,' ')+"  "+DCUtils.rpad("Avail",7,' ')+"  "+DCUtils.rpad("Fut_Av",7,' ')+
                        "  "+DCUtils.rpad("AvailFrom",20,' ')+"  "+DCUtils.rpad("AvailUntil",20,' ')+"  "+DCUtils.rpad("Durat",7,' ')+
                        "");
                LOG.debug(
                        DCUtils.rpad("-",13,'-')+"  "+DCUtils.rpad("-",30,'-')+
                        "  "+DCUtils.rpad("-",17,'-')+"  "+DCUtils.rpad("-",17,'-')+"  "+DCUtils.rpad("-",15,'-')+
                        "  "+DCUtils.rpad("-", 7,'-')+"  "+DCUtils.rpad("-", 7,'-')+"  "+DCUtils.rpad("-", 7,'-')+
                        "  "+DCUtils.rpad("-",20,'-')+"  "+DCUtils.rpad("-",20,'-')+"  "+DCUtils.rpad("-", 7,'-')+
                        "");
//                Map<String, LocationDto> m = bzDto.getLocationMap();
//                for (String n : m.keySet()) {
//                    LocationDto l = m.get(n);
//                    String name = l.getName();
//                    LOG.debug(
//                            DCUtils.rpad(" ",13,' ')+"  "+DCUtils.rpad(name,30,' ')+
//                            "  "+DCUtils.rpad(l.getLatitude(),17,' ')+"  "+DCUtils.rpad(l.getLongitude(),17,' ')+"  "+DCUtils.rpad(l.getKind(),15,' ')+
//                            "");
//                    for (BikesharingBzBayDto b : fetchData) {
//                        LocationDto bl = b.getLocation();
//                        String bname = bl.getName();
//                        if ( name.equals(bname) ) {
//                            LOG.debug(
//                                    DCUtils.lpad(b.getLicense(),2,' ')+"-"+DCUtils.lpad(b.getId(),10,' ')+"  "+DCUtils.rpad(l.getName(),30,' ')+
//                                    "  "+DCUtils.rpad(bl.getLatitude(),17,' ')+"  "+DCUtils.rpad(bl.getLongitude(),17,' ')+"  "+DCUtils.rpad(bl.getKind(),15,' ')+
//                                    "  "+DCUtils.rpad(b.getInMaintenance(),7,' ')+"  "+DCUtils.rpad(b.getAvailability(),7,' ')+"  "+DCUtils.rpad(b.getFutureAvailability(),7,' ')+
//                                    "  "+DCUtils.rpad(DCUtils.convertDateToString(b.getAvailableFrom(),"yyyy-MM-dd_HH:mm:ss"),20,' ')+"  "+DCUtils.rpad(DCUtils.convertDateToString(b.getAvailableUntil(),"yyyy-MM-dd_HH:mm:ss"),20,' ')+"  "+DCUtils.rpad(b.getAvailableDuration(),7,' ')+
//                                    ""
//                                    );
//                        }
//                    }
//                }
            }

        } catch (Exception e) {
            String msg = "Exception in testFetchData: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }
    }

    @Test
    public void testFetchDataTypes() {
        try {
            //Fetch data from source origin
            List<DataTypeDto> fetchData = pusher.mapDataTypes2Bdp();

            //Check there is at least one item in the list
            assertNotNull("Fetched data IS NULL", fetchData);
            if ( fetchData.size() == 0 ) {
                Assert.fail("Fetched data IS EMPTY");
            }

        } catch (Exception e) {
            String msg = "Exception in testFetchData: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }
    }

}
