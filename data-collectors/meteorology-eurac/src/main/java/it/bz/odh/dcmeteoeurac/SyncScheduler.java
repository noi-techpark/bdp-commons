package it.bz.odh.dcmeteoeurac;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.odh.dcmeteoeurac.dto.ClimatologyDto;
import it.bz.odh.dcmeteoeurac.dto.MetadataDto;

@Service
public class SyncScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

    private static final String STATION_ID_PREFIX = "EURAC_";

    private static final String DATATYPE_ID_TMIN = "air-temperature-min";
    private static final String DATATYPE_ID_TMAX = "air-temperature-max";
    private static final String DATATYPE_ID_TMEAN = "air-temperature";
    private static final String DATATYPE_ID_PREC = "precipitation";

    @Autowired
    private Environment env;

    @Value("${odh_client.period.climatology}")
    private Integer climatologyPeriod;

    @Value("${odh_client.period.climateDaily}")
    private Integer climateDailyPeriod;

    @Lazy
    @Autowired
    private OdhClient odhClient;

    /**
     * Scheduled job A: sync stations and data types
     * 
     * @throws IOException
     */
    @Scheduled(cron = "${scheduler.job_stations}")
    public void syncJobStations() throws IOException {
        LOG.info("Cron job A started: Sync Stations with type {} and data types", odhClient.getIntegreenTypology());

        List<DataTypeDto> odhDataTypeList = new ArrayList<>();
        odhDataTypeList.add(new DataTypeDto(DATATYPE_ID_TMIN, "°C", "Minimum temperature", "min"));
        odhDataTypeList.add(new DataTypeDto(DATATYPE_ID_TMAX, "°C", "Maximum temperature", "max"));
        odhDataTypeList.add(new DataTypeDto(DATATYPE_ID_TMEAN, "°C", "Mean temperature", "mean"));
        odhDataTypeList.add(new DataTypeDto(DATATYPE_ID_PREC, "mm", "Precipitation", "total"));

        ObjectMapper objectMapper = new ObjectMapper();

        String stationsUrl = env.getProperty("endpoint.stations.url");

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet(stationsUrl));
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        MetadataDto[] euracStations = objectMapper.readValue(responseString, MetadataDto[].class);

        StationList odhStationList = new StationList();
        for (MetadataDto s : euracStations) {
            StationDto station = new StationDto(STATION_ID_PREFIX + s.getId(), s.getName(), s.getLat(), s.getLon());

            station.setOrigin(odhClient.getProvenance().getLineage());
            odhStationList.add(station);
        }

        try {
            odhClient.syncStations(odhStationList);
            odhClient.syncDataTypes(odhDataTypeList);
            LOG.info("Cron job for stations successful");
        } catch (WebClientRequestException e) {
            LOG.error("Cron job for stations failed: Request exception: {}", e.getMessage());
        }
    }

    /**
     * Scheduled job B: sync monthly climatologies @throws IOException @throws
     * 
     * @throws IOException
     */
    @Scheduled(cron = "${scheduler.job_climatologies}")
    public void syncJobClimatologies() throws IOException {
        LOG.info("Cron job B started: Pushing climatology measurements for {}", odhClient.getIntegreenTypology());

        ObjectMapper objectMapper = new ObjectMapper();

        String climatologiesUrl = env.getProperty("endpoint.climatologies.url");

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet(climatologiesUrl));
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
        ClimatologyDto[] climatologies = objectMapper.readValue(responseString, ClimatologyDto[].class);

        int prevStationId = -1;
        DataMapDto<RecordDtoImpl> stationMap = null;
        DataMapDto<RecordDtoImpl> tMinMetricMap = null;
        DataMapDto<RecordDtoImpl> tMaxMetricMap = null;
        DataMapDto<RecordDtoImpl> tMeanMetricMap = null;
        DataMapDto<RecordDtoImpl> precMetricMap = null;

        int timestampYear = Year.now().getValue() - 1;

        for (ClimatologyDto climatology : climatologies) {
            if (prevStationId != climatology.getId()) {
                stationMap = rootMap.upsertBranch(STATION_ID_PREFIX + climatology.getId());

                tMinMetricMap = stationMap.upsertBranch(DATATYPE_ID_TMIN);
                tMaxMetricMap = stationMap.upsertBranch(DATATYPE_ID_TMAX);
                tMeanMetricMap = stationMap.upsertBranch(DATATYPE_ID_TMEAN);
                precMetricMap = stationMap.upsertBranch(DATATYPE_ID_PREC);

                prevStationId = climatology.getId();
            }

            int timestampMonth = climatology.getMonth();
            LocalDateTime localDateTime = LocalDateTime.of(timestampYear, timestampMonth, 1, 0, 0);
            long timestamp = localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

            addMeasurementToMap(tMinMetricMap, new SimpleRecordDto(timestamp, climatology.getTmin()),
                    climatologyPeriod);
            addMeasurementToMap(tMaxMetricMap, new SimpleRecordDto(timestamp, climatology.getTmax()),
                    climatologyPeriod);
            addMeasurementToMap(tMeanMetricMap, new SimpleRecordDto(timestamp, climatology.getTmean()),
                    climatologyPeriod);
            addMeasurementToMap(precMetricMap, new SimpleRecordDto(timestamp, climatology.getPrec()),
                    climatologyPeriod);
        }

        // Send the measurements to the Open Data Hub INBOUND API (writer)
        // WARNING: stations and datatypes must already exist, otherwise this call will
        // fail
        // It does not throw any exception, it will just not insert that data (this is a
        // known issue)
        // Exception will only be thrown on connection errors here! Please refer to the
        // writer log output or the database itself to see if data has been inserted
        try {
            odhClient.pushData(rootMap);
            LOG.info("Cron job for climatologies successful");
        } catch (WebClientRequestException e) {
            LOG.error("Cron job for climatologies failed: Request exception: {}", e.getMessage());
        }
    }

    private void addMeasurementToMap(DataMapDto<RecordDtoImpl> map, SimpleRecordDto measurement, int period) {
        if (map != null) {
            measurement.setPeriod(period);
            map.getData().add(measurement);
        }
    }
}
