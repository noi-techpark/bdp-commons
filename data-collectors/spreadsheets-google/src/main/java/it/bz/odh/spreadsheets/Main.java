package it.bz.odh.spreadsheets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.odh.spreadsheets.dto.DataTypeWrapperDto;
import it.bz.odh.spreadsheets.dto.MappingResult;
import it.bz.odh.spreadsheets.services.GoogleSpreadSheetDataFetcher;
import it.bz.odh.spreadsheets.services.ODHClient;
import it.bz.odh.spreadsheets.util.DataMappingUtil;

@Service
public class Main {
    
    @Lazy
    @Autowired
    private GoogleSpreadSheetDataFetcher googleClient;

    @Lazy
    @Autowired
    private ODHClient odhClient;

    @Autowired
    private DataMappingUtil mappingUtil;
    
    @Value("${spreadsheetId}")
    private String origin;

    /**
     * scheduled job which syncs odh with the spreadsheet
     */
    public void syncData() {
        Spreadsheet fetchedSpreadSheet = (Spreadsheet) googleClient.fetchSheet();
        StationList dtos = new StationList();
        List <DataTypeWrapperDto> types = new ArrayList<DataTypeWrapperDto>();
        for (Sheet sheet : fetchedSpreadSheet.getSheets()){
            try {
                List<List<Object>> values = googleClient.getWholeSheet(sheet.getProperties().getTitle()).getValues();
                if (values.isEmpty() || values.get(0) == null)
                    throw new IllegalStateException("Spreadsheet "+sheet.getProperties().getTitle()+" has no header row. Needs to start on top left.");
                MappingResult result = mappingUtil.mapSheet(values,sheet);
                if (!result.getStationDtos().isEmpty())
                    dtos.addAll(result.getStationDtos());
                if (result.getDataType() != null) {
                    types.add(result.getDataType());
                }
            }catch(Exception ex) {
                ex.printStackTrace();
                continue;
            }
        }
        if (!dtos.isEmpty())
            odhClient.syncStations(dtos);
        if (!types.isEmpty()) {
            List<DataTypeDto> dTypes = types.stream().map(mapper).collect(Collectors.toList());
            odhClient.syncDataTypes(dTypes);
        }
        DataMapDto<? extends RecordDtoImpl> dto = new DataMapDto<RecordDtoImpl>();
        for (DataTypeWrapperDto typeDto: types) {
            SimpleRecordDto simpleRecordDto = new SimpleRecordDto(new Date().getTime(), typeDto.getSheetName(),0);
            dto.addRecord(dtos.get(0).getId(), typeDto.getType().getName(), simpleRecordDto);
        }
        odhClient.pushData(dto);
    }
    Function<DataTypeWrapperDto,DataTypeDto> mapper = (dto) -> {
        return dto.getType();
    };

}
