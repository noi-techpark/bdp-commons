package it.bz.idm.bdp.augeg4.fun.push;

import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.Measurement;
import it.bz.idm.bdp.augeg4.face.DataPusherMapperFace;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;

import java.util.Date;
import java.util.List;

public class DataPusherMapper implements DataPusherMapperFace {

    private final int period;

    DataPusherMapper(int period) {
        this.period = period;
    }

    /**
     * @inheritDoc
     */
    @Override
    public DataMapDto<RecordDtoImpl> map(List<AugeG4ToHubDataDto> measurementsByStations) {
        DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
        for (AugeG4ToHubDataDto measurementsByStation : measurementsByStations) {
            mapMeasurementByStation(rootMap, measurementsByStation);
        }
        return rootMap;
    }

    private void mapMeasurementByStation(DataMapDto<RecordDtoImpl> rootMap, AugeG4ToHubDataDto measurementsByStation) {
        String stationIdentifier = measurementsByStation.getStation();
        DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(stationIdentifier);
        for (Measurement measurement : measurementsByStation.getMeasurements()) {
            mapMeasurement(measurementsByStation, stationMap, measurement);
        }
    }

    private void mapMeasurement(AugeG4ToHubDataDto measurementsByStation, DataMapDto<RecordDtoImpl> stationMap, Measurement measurement) {
        String resourceDataType = measurement.getDataType();
        DataMapDto<RecordDtoImpl> parametersMap = stationMap.upsertBranch(resourceDataType);
        List<RecordDtoImpl> values = parametersMap.getData();
        Date recordDate = measurementsByStation.getAcquisition();
        SimpleRecordDto record = getSimpleRecordDto(measurement, recordDate);
        values.add(record);
    }

    private SimpleRecordDto getSimpleRecordDto(Measurement measurement, Date recordDate) {
        SimpleRecordDto record = new SimpleRecordDto(recordDate.getTime(), measurement.getValue());
        record.setPeriod(period);
        return record;
    }
}
