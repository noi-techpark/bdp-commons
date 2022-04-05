package it.bz.idm.bdp.dcparkingtn;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import it.bz.idm.bdp.dcparkingtn.dto.ParkingTnDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.util.NominatimException;
import it.bz.idm.bdp.util.NominatimLocationLookupUtil;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
public class ParkingTnJobScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingTnJobScheduler.class.getName());

    @Autowired
    private ParkingTnDataPusher pusher;

    @Autowired
    private ParkingTnDataRetriever retrieval;

	private NominatimLocationLookupUtil lookupUtil = new NominatimLocationLookupUtil();

    /** JOB 1 */
    public void pushStations() throws Exception {
        LOG.info("START.pushStations");
        
        // to log how many stations have been pushed
        int stationCounter = -1;
        try {
            List<ParkingTnDto> data = retrieval.fetchData();
            patchMunicipality(data);
            StationList stations = pusher.mapStations2Bdp(data);
            if (stations != null) {
                stationCounter = stations.size();
                pusher.syncStations(stations);
            }
        } catch (HttpClientErrorException e) {
            LOG.error(pusher + " - " + e + " - " + e.getResponseBodyAsString(), e);
        }
        LOG.info("END.pushStations amount: " + stationCounter);
    }

    private void patchMunicipality(List<ParkingTnDto> data) throws NominatimException {
		for (ParkingTnDto dto : data) {
			if (dto.getParkingArea() != null && dto.getParkingArea().getPosition() != null && dto.getParkingArea().getPosition().size() == 2) {
				String lookupLocation = lookupUtil.lookupLocation(dto.getParkingArea().getPosition().get(1),dto.getParkingArea().getPosition().get(0));
				dto.getStation().getMetaData().put("municipality", lookupLocation);
			}
		}

	}

	/** JOB 2 */
    public void pushData() throws Exception {
        LOG.info("START.pushData");
        
        // to log how many data records have been pushed
        int dataCounter = -1;
        try {
            List<ParkingTnDto> data = retrieval.fetchData();

            dataCounter = data.size();

            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);

            if (stationRec != null){
                pusher.pushData(stationRec);
            }

        } catch (Exception e) {
            LOG.error(pusher + " - " + e, e);
            throw e;
        }
        LOG.info("END.pushData amount: " + dataCounter);
    }
}