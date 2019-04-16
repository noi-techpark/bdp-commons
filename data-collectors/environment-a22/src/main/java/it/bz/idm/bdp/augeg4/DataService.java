package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.face.DataConverterFace;
import it.bz.idm.bdp.augeg4.face.DataLinearizerFace;
import it.bz.idm.bdp.augeg4.face.DataPusherFace;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

// TODO: add buffer for mqtt inbound messages.
public class DataService {

    private static final Logger LOG = LogManager.getLogger(DataService.class.getName());

    private DataPusherFace pusher;

    private DataRetrieverFace retrieval;

    private DataConverterFace converter;

    private DataLinearizerFace linearizer;

    List<AugeG4LinearizedDataDto> linearizedData;
    StationList stationList = new StationList();
    List<DataTypeDto> dataTypeList = new ArrayList<DataTypeDto>();


    public DataService(DataPusherFace pusher, DataRetrieverFace retrieval, DataLinearizerFace linearizer, DataConverterFace converter) {
        this.pusher = pusher;
        this.retrieval = retrieval;
        this.linearizer = linearizer;
        this.converter = converter;
    }

    public void pushStations(String stationType, String origin) throws Exception {
        LOG.info("pushStations() called");

        List<AugeG4ToHubDataDto> data = new ArrayList<>();
        for (AugeG4ToHubDataDto dto : data) {
            StationDto station = new StationDto();
            station.setId(dto.getStation());
            station.setName("Cool non-unique name for ID " + dto.getStation());
            station.setStationType(stationType);
            station.setOrigin(origin); // The source of our data set
            stationList.add(station);
        }

		try {
			pusher.syncStations(stationList);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    }

    public void pushDataTypes(Integer period) throws Exception {
        LOG.info("pushDataTypes() called.");

        List<AugeG4ToHubDataDto> data = new ArrayList<>();
        for (AugeG4ToHubDataDto dto : data) {
            DataTypeDto type = new DataTypeDto();
            //type.setName(dto.getName());
            type.setPeriod(period);
            //type.setUnit(dto.getUnit());
            dataTypeList.add(type);
        }

		pusher.syncDataTypes(dataTypeList);
    }

    /** JOB 3 */
    public void pushData() throws Exception {
        LOG.info("pushData() called.");
        List<AugeG4FromAlgorabDataDto> data = retrieval.fetchData();
		try {
			pusher.mapData(converter.convert(linearizer.linearize(data)));
			pusher.pushData();
		} catch (Exception e) {
			LOG.error("Processing of failed: {}.", e.getMessage());
			throw e;
		}
    }


}
