// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.spreadsheets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import it.bz.odh.spreadsheets.mapper.DynamicMapper;
import it.bz.odh.spreadsheets.services.GoogleSpreadSheetDataFetcher;
import it.bz.odh.spreadsheets.services.ODHClient;

@Lazy
@Service
public class DefaultCollector implements ISpreadsheetCollector {
    private Logger logger = LoggerFactory.getLogger(DefaultCollector.class);

    @Lazy
    @Autowired
    private GoogleSpreadSheetDataFetcher googleClient;

    @Lazy
    @Autowired
    private ODHClient odhClient;

    @Autowired
    private DynamicMapper mappingUtil;

    /**
     * scheduled job which syncs odh with the spreadsheet
     */
    public void syncData() {
        logger.info("Start data syncronization");
        Spreadsheet fetchedSpreadSheet = (Spreadsheet) googleClient.fetchSheet();
        StationList dtos = new StationList();
        List<DataTypeWrapperDto> types = new ArrayList<DataTypeWrapperDto>();
        logger.debug("Start reading spreadsheet");
        for (Sheet sheet : fetchedSpreadSheet.getSheets()) {
            try {
                List<List<Object>> values = googleClient.getWholeSheet(sheet.getProperties().getTitle()).getValues();
                if (values.isEmpty() || values.get(0) == null)
                    throw new IllegalStateException("Spreadsheet " + sheet.getProperties().getTitle()
                            + " has no header row. Needs to start on top left.");
                logger.debug("Starting to map sheet using mapper: " + mappingUtil.getClass().getCanonicalName());
                MappingResult result = mappingUtil.mapSheet(values, sheet);
                if (!result.getStationDtos().isEmpty())
                    dtos.addAll(result.getStationDtos());
                if (result.getDataType() != null) {
                    types.add(result.getDataType());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.debug("Failed to read sheet(tab). Start reading next");
                continue;
            }
        }
        if (!dtos.isEmpty()) {
            logger.debug("Syncronize stations if some where fetched and successfully parsed");
            odhClient.syncStations(dtos);
            logger.debug("Syncronize stations completed");
        }
        if (!types.isEmpty()) {
            logger.debug("Syncronize data types/type-metadata if some where fetched and successfully parsed");
            List<DataTypeDto> dTypes = types.stream().map(mapper).collect(Collectors.toList());
            odhClient.syncDataTypes(dTypes);
            logger.debug("Syncronize datatypes completed");
        }
        if (!dtos.isEmpty() && !types.isEmpty()) {
            DataMapDto<? extends RecordDtoImpl> dto = new DataMapDto<RecordDtoImpl>();
            logger.debug("Connect datatypes with stations through record");
            for (DataTypeWrapperDto typeDto : types) {
                SimpleRecordDto simpleRecordDto = new SimpleRecordDto(new Date().getTime(), typeDto.getSheetName(), 0);
                logger.trace("Connect" + dtos.get(0).getId() + "with" + typeDto.getType().getName());
                dto.addRecord(dtos.get(0).getId(), typeDto.getType().getName(), simpleRecordDto);
            }
            odhClient.pushData(dto);
        }
        logger.info("Data syncronization completed");
    }

    private Function<DataTypeWrapperDto, DataTypeDto> mapper = (dto) -> {
        return dto.getType();
    };
}
