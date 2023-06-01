// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.onstreetparkingbz;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dconstreetparkingbz.OnstreetParkingBzDataConverter;
import it.bz.idm.bdp.dconstreetparkingbz.OnstreetParkingBzDataPusher;
import it.bz.idm.bdp.dto.DataTypeDto;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class OnstreetParkingBzDataPusherIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(OnstreetParkingBzDataPusherIT.class.getName());

    @Value("${spreadsheet.sheetName}")
    private String SHEETNAME;

    @Autowired
    private OnstreetParkingBzDataConverter converter;

    @Autowired
    private OnstreetParkingBzDataPusher pusher;

    private boolean doPush = true;

    @Test
    public void testPushController() {
        String methodName = "testPushController";
        LOG.debug("START."+methodName);
        if ( !doPush ) {
            LOG.debug("Exit, doPush="+doPush);
            return;
        }

        try {
//            //Sync Station "sensor-1"
//            StationList stations = new StationList();
//            StationDto stationDto = new StationDto();
//            stationDto.setId(OnstreetParkingBzDataConverterTest.TEST_STATION_ID_1);
//            stationDto.setName(OnstreetParkingBzDataConverterTest.TEST_STATION_ID_1);
//            stationDto.setOrigin(DCUtils.trunc(converter.getOrigin(), 255));
//            stationDto.setStationType(converter.getStationType());
//            stationDto.setLatitude(46.494825D);
//            stationDto.setLongitude(11.33998888888889D);
//            stations.add(stationDto);
//            pusher.syncStations(stations);
//
//            //Sync DataType "occupied"
//            List<DataTypeDto> dataTypes = pusher.mapDataTypes2Bdp();
//            pusher.syncDataTypes(dataTypes);

            //Get json string and push data to the controller
            String jsonStr = OnstreetParkingBzDataConverterTestIT.getTestData(OnstreetParkingBzDataConverterTestIT.DATA_PUSH_MEASUREMENTS_2, null, null);
            //String url = OnstreetParkingBzDataConverterTest.CONTROLLER_URL;
            
            String url = "https://www.edp-progetti.it:443/parking";
            LOG.info("url = '"+url+"'");
            LOG.info("jsonStr = '"+jsonStr+"'");

            HttpClientBuilder builder = HttpClients.custom();
            CloseableHttpClient client = builder.build();
            HttpPost httpPost = new HttpPost(url);

            StringEntity postEntity = new StringEntity(jsonStr);
            httpPost.setEntity(postEntity);

            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            InputStream is = responseEntity.getContent();
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer);
            String responseData = writer.toString();
            response.close();

            LOG.info("responseData = '"+responseData+"'");

        } catch (Exception ex) {
            LOG.error("Exception in "+methodName+": "+ex, ex);
            Assert.fail();
        }
        LOG.debug("END."+methodName);
    }

}
