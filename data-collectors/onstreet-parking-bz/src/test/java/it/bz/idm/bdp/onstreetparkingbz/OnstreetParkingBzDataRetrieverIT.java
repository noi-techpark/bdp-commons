package it.bz.idm.bdp.onstreetparkingbz;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.swagger.annotations.ApiModelProperty;
import it.bz.idm.bdp.dconstreetparkingbz.DCUtils;
import it.bz.idm.bdp.dconstreetparkingbz.OnstreetParkingBzDataPusher;
import it.bz.idm.bdp.dconstreetparkingbz.OnstreetParkingBzDataRetriever;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class OnstreetParkingBzDataRetrieverIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(OnstreetParkingBzDataRetrieverIT.class.getName());

    @Autowired
    private OnstreetParkingBzDataRetriever reader;

    @Autowired
    private OnstreetParkingBzDataPusher pusher;

    @Test
    public void testFetchData() {
        try {
            //Fetch data from source origin
            StationList stationList = reader.fetchStations();

            //Check there is at least one item in the list
            assertNotNull("Fetched data IS NULL", stationList);
            if ( stationList.size() == 0 ) {
                Assert.fail("Fetched data IS EMPTY");
            }

            //Log all formatted data, only if DEBUG is enabled
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(
                        DCUtils.rpad("id",10,' ')+"  "+DCUtils.rpad("stationType",20,' ')+"  "+DCUtils.rpad("name",30,' ')+"  "+
                        DCUtils.rpad("Latitude",17,' ')+"  "+DCUtils.rpad("Longitude",17,' ')+"  "+DCUtils.rpad("elevation",17,' ')+"  "+
                        DCUtils.rpad("origin",15,' ')+"  "+DCUtils.rpad("parentStation",10,' ')+"  "+
                        "");
                LOG.debug(
                        DCUtils.rpad("-",10,'-')+"  "+DCUtils.rpad("-",20,'-')+"  "+DCUtils.rpad("-",30,'-')+"  "+
                        DCUtils.rpad("-",17,'-')+"  "+DCUtils.rpad("-",17,'-')+"  "+DCUtils.rpad("-",17,'-')+"  "+
                        DCUtils.rpad("-",15,'-')+"  "+DCUtils.rpad("-",10,'-')+"  "+
                        "");
                for (StationDto s : stationList) {
                    LOG.debug(
                            DCUtils.lpad(s.getId(),10,' ')+"  "+DCUtils.lpad(s.getStationType(),20,' ')+"  "+DCUtils.rpad(s.getName(),30,' ')+"  "+
                            DCUtils.rpad(s.getLatitude(),17,' ')+"  "+DCUtils.rpad(s.getLongitude(),17,' ')+"  "+DCUtils.rpad(s.getElevation(),17,' ')+
                            DCUtils.rpad(s.getOrigin(),15,' ')+"  "+DCUtils.rpad(s.getParentStation(),10,' ')+
                            ""
                            );
                }
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
