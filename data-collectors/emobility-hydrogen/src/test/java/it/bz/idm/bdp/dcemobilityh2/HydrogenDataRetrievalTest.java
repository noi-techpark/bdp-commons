package it.bz.idm.bdp.dcemobilityh2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcemobilityh2.dto.HydrogenDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class HydrogenDataRetrievalTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private HydrogenDataPusher pusher;

    @Autowired
    private HydrogenDataRetriever reader;

    public static String TEST_RESPONSE_STRING =
            "<ceemes version=\"1.0\">\n" + 
            "    <fuelstation>\n" + 
            "        <idx>326</idx>\n" + 
            "        <name>Bozen (H2 Center)</name>\n" + 
            "        <operatorname>iit – Institut für innovative Technologien\n" + 
            "        </operatorname>\n" + 
            "        <hostname>iit – Institut für innovative Technologien</hostname>\n" + 
            "        <street>Via Enrico Mattei</street>\n" + 
            "        <streetnr>1</streetnr>\n" + 
            "        <zip>39100</zip>\n" + 
            "        <city>Bozen</city>\n" + 
            "        <countryshortname>IT</countryshortname>\n" + 
            "        <latitude>46.475093</latitude>\n" + 
            "        <longitude>11.318306</longitude>\n" + 
            "        <has_shop>f</has_shop>\n" + 
            "        <image>697</image>\n" + 
            "        <maintenance_start>2018-06-05 08:30:00</maintenance_start>\n" + 
            "        <maintenance_end>2018-06-12 17:00:00</maintenance_end>\n" + 
            "        <has_350_large>f</has_350_large>\n" + 
            "        <has_350_small>f</has_350_small>\n" + 
            "        <operatorhotline>+39 366 578 46 02</operatorhotline>\n" + 
            "        <operatorlogo>700</operatorlogo>\n" + 
            "        <hostlogo>700</hostlogo>\n" + 
            "        <combinedstatus>OPEN</combinedstatus>\n" + 
            "        <opening_hours>24 Stunden täglich geöffnet</opening_hours>\n" + 
            "        <fundingpage />\n" + 
            "        <comments>24 Stunden Betrieb für Selbstbetankung nach Registrierung\n" + 
            "            &amp; Schulung&#13;\n" + 
            "            Bediente Betankung Mo-Fr. 8-18 Uhr (kurze\n" + 
            "            telefonischer Ankündigung\n" + 
            "            erwünscht) &#13;\n" + 
            "            Spezielle Tankzeiten &amp;\n" + 
            "            Besichtigung H2 Zentrum auf Anfrage.&#13;\n" + 
            "            &#13;\n" + 
            "            +39 366 578 46 02\n" + 
            "        </comments>\n" + 
            "    </fuelstation>\n" + 
            "    <fuelstation>\n" + 
            "        <idx>327</idx>\n" + 
            "        <name>Bozen (TEST2 Center)</name>\n" + 
            "        <operatorname>iit – Institut für innovative Technologien\n" + 
            "        </operatorname>\n" + 
            "        <hostname>iit – Institut für innovative Technologien</hostname>\n" + 
            "        <street>Via Roma</street>\n" + 
            "        <streetnr>1</streetnr>\n" + 
            "        <zip>39100</zip>\n" + 
            "        <city>Bozen</city>\n" + 
            "        <countryshortname>IT</countryshortname>\n" + 
            "        <latitude>46.475093</latitude>\n" + 
            "        <longitude>11.318306</longitude>\n" + 
            "        <has_shop>f</has_shop>\n" + 
            "        <image>697</image>\n" + 
            "        <maintenance_start>2018-06-05 08:30:00</maintenance_start>\n" + 
            "        <maintenance_end>2018-06-12 17:00:00</maintenance_end>\n" + 
            "        <has_350_large>f</has_350_large>\n" + 
            "        <has_350_small>f</has_350_small>\n" + 
            "        <operatorhotline>+39 366 578 46 02</operatorhotline>\n" + 
            "        <operatorlogo>700</operatorlogo>\n" + 
            "        <hostlogo>700</hostlogo>\n" + 
            "        <combinedstatus>OPEN</combinedstatus>\n" + 
            "        <opening_hours>Open 24 hours a day</opening_hours>\n" + 
            "        <fundingpage />\n" + 
            "        <comments>24 hours self-service operation after registration &amp;\n" + 
            "            training&#13;\n" + 
            "            Assisted refueling Mo-Fr. 8-18 o'clock (short telephone\n" + 
            "            announcement\n" + 
            "            required)&#13;\n" + 
            "            Special refilling times &amp; sightseeing\n" + 
            "            H2 center on request.&#13;\n" + 
            "            &#13;\n" + 
            "            +39 366 578 46 02\n" + 
            "        </comments>\n" + 
            "    </fuelstation>\n" + 
            "</ceemes>";

    @Test
    public void testFetchData() {
        try {
            List<HydrogenDto> fetchData = reader.fetchData();
            assertEquals(4, fetchData.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testConvertData() {

        try {
            String responseString = TEST_RESPONSE_STRING;

            List<HydrogenDto> data = reader.convertResponseToInternalDTO(responseString);

            StationList stations = pusher.mapStations2Bdp(data);
            StationList plugs    = pusher.mapPlugs2Bdp(data);
            DataMapDto<RecordDtoImpl> map = pusher.mapData(data);
            DataMapDto<RecordDtoImpl> plugRec = pusher.mapPlugData2Bdp(data);

            assertEquals(2, stations.size());
            assertEquals(2, plugs.size());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
}
