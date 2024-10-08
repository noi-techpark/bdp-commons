// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcparkingtn;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dcparkingtn.dto.ParkingTnDto;
import it.bz.idm.bdp.dcparkingtn.metadata.MetadataEnrichment;
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

    @Autowired
    private MetadataEnrichment metadataEnrichment;
    
    @Autowired
    private ParkingTnDataConverter converter;

    private NominatimLocationLookupUtil lookupUtil = new NominatimLocationLookupUtil();

    /** JOB 1 */
    public void pushStations() throws Exception {
        LOG.info("START.pushStations");

        // to log how many stations have been pushed
        int stationCounter = -1;
        StationList stations;
        try {
            List<ParkingTnDto> data = retrieval.fetchData();
            patchMunicipality(data);
            stations = pusher.mapStations2Bdp(data);
        } catch (Exception e) {
            LOG.error("Error getting stations from endpoint. Continuing by updating existing stations");
            stations = new StationList(
                    pusher.fetchStations(converter.getStationType(), converter.getOrigin()));
        }

        metadataEnrichment.mapData(stations);

        if (stations != null) {
            stationCounter = stations.size();
            pusher.syncStations(stations);
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
