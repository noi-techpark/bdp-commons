package it.bz.idm.bdp.augeg4.fun.push;

import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.Measurement;
import it.bz.idm.bdp.augeg4.dto.tohub.StationId;
import it.bz.idm.bdp.augeg4.face.DataPusherMapperFace;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataPusherMapper implements DataPusherMapperFace {

    private static final String DATA_TYPE_NAME_SUFFIX_RAW = "_raw";
    private static final String DATA_TYPE_NAME_SUFFIX_PROCESSED = "_processed";

    private final int period;

    DataPusherMapper(int period) {
        this.period = period;
    }

    /**
     * @inheritDoc
     */
    @Override
    public DataMapDto<RecordDtoImpl> mapData(List<AugeG4ToHubDataDto> measurementsByStations) {
        DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
        measurementsByStations.forEach(measurementsByStation -> mapMeasurementByStation(rootMap, measurementsByStation));
        return rootMap;
    }

    private void mapMeasurementByStation(DataMapDto<RecordDtoImpl> rootMap, AugeG4ToHubDataDto measurementsByStation) {
        StationId stationId = measurementsByStation.getStationId();
        DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(stationId.getValue());
        measurementsByStation.getMeasurements()
                .forEach(measurement -> {
                    mapRawMeasurement(measurementsByStation, stationMap, measurement);
                    mapProcessedMeasurement(measurementsByStation, stationMap, measurement);
                });
    }

    private void mapRawMeasurement(AugeG4ToHubDataDto measurementsByStation, DataMapDto<RecordDtoImpl> stationMap, Measurement measurement) {
        String dataType = measurement.getDataType() + DATA_TYPE_NAME_SUFFIX_RAW;
        List<RecordDtoImpl> values = getRecordDtoList(stationMap, dataType);
        values.add(getSimpleRecordDto(
                measurement.getRawValue(),
                measurementsByStation.getAcquisition()
        ));
    }

    private void mapProcessedMeasurement(AugeG4ToHubDataDto measurementsByStation, DataMapDto<RecordDtoImpl> stationMap, Measurement measurement) {
        String dataType = measurement.getDataType() + DATA_TYPE_NAME_SUFFIX_PROCESSED;
        List<RecordDtoImpl> values = getRecordDtoList(stationMap, dataType);
        values.add(getSimpleRecordDto(
                measurement.getProcessedValue(),
                measurementsByStation.getAcquisition()
        ));
    }

    private List<RecordDtoImpl> getRecordDtoList(DataMapDto<RecordDtoImpl> stationMap, String dataType) {
        DataMapDto<RecordDtoImpl> parametersMap = stationMap.upsertBranch(dataType);
        return parametersMap.getData();
    }

    private SimpleRecordDto getSimpleRecordDto(double value, Date recordDate) {
        SimpleRecordDto record = new SimpleRecordDto(recordDate.getTime(), value);
        record.setPeriod(period);
        return record;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<DataTypeDto> mapDataTypes(List<DataTypeDto> dataTypeDtoList) {
        List<DataTypeDto> mapped = new ArrayList<>();
        dataTypeDtoList.forEach(dataTypeDto -> {
            mapped.add(getRawDataType(dataTypeDto));
            mapped.add(getProcessedDataType(dataTypeDto));
        });
        return mapped;
    }

    private DataTypeDto getRawDataType(DataTypeDto dataTypeDto) {
        return new DataTypeDto(
                dataTypeDto.getName() + DATA_TYPE_NAME_SUFFIX_RAW,
                null,
                dataTypeDto.getDescription(),
                dataTypeDto.getRtype(),
                dataTypeDto.getPeriod()
        );
    }

    private DataTypeDto getProcessedDataType(DataTypeDto dataTypeDto) {
        return new DataTypeDto(
                dataTypeDto.getName() + DATA_TYPE_NAME_SUFFIX_PROCESSED,
                dataTypeDto.getUnit(),
                dataTypeDto.getDescription(),
                dataTypeDto.getRtype(),
                dataTypeDto.getPeriod()
        );
    }
}
