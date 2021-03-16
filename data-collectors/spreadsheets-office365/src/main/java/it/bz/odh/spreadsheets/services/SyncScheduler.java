package it.bz.odh.spreadsheets.services;

import it.bz.idm.bdp.dto.*;
import it.bz.odh.spreadsheets.dto.DataTypeWrapperDto;
import it.bz.odh.spreadsheets.dto.MappingResult;
import it.bz.odh.spreadsheets.utils.microsoft.WorkbookUtil;
import it.bz.odh.spreadsheets.utils.DataMappingUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

// The scheduler could theoretically be replaced by Microsoft Graphs Change Notifications
// So you don't need to poll last date changed with a cron job, but get notified, when changes are made
//
// Change notifications docs:
// https://docs.microsoft.com/en-us/graph/api/resources/webhooks?view=graph-rest-1.0
//
// StackExchange discussion about change notifications with Sharepoint
// https://sharepoint.stackexchange.com/questions/264609/does-the-microsoft-graph-support-driveitem-change-notifications-for-sharepoint-o


@Service
public class SyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SyncScheduler.class);

    private Function<DataTypeWrapperDto, DataTypeDto> mapper = (dto) -> {
        return dto.getType();
    };

    @Lazy
    @Autowired
    private ODHClient odhClient;

    @Autowired
    private WorkbookUtil workbookUtil;

    @Autowired
    private DataMappingUtil mappingUtil;

    /**
     * Cron job to check changes of the Spreadsheet in Sharepoint
     * If changes where made, data gets uploaded to the BDP
     *
     * @throws Exception
     */
    @Scheduled(cron = "${cron}")
    public void checkSharepoint() throws Exception {
        logger.info("Cron job manual sync started");
        Workbook sheet = workbookUtil.checkWorkbook();

        if (sheet != null) {
            logger.info("Syncing data with BDP");
            syncDataWithBdp(sheet);
            logger.info("Done: Syncing data with BDP");
        } else
            logger.info("No new changes detected, skip sync with BDP");

        logger.info("Cron job manual sync end");
    }


    /**
     * Converts a XSSFWorkbook to BDP Stations
     */
    private void syncDataWithBdp(Workbook workbook) {
        // fetch sheet
        //read from disk
        // iterate over values and
        logger.info("Start data syncronization");
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        StationList dtos = new StationList();
        List<DataTypeWrapperDto> types = new ArrayList<DataTypeWrapperDto>();
        logger.debug("Start reading spreadsheet");
        int index = 0;
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();

            //convert values of sheet to List<List<Object>> to be able to map data with data-mapping of dc-googlespreadsheets
            List<List<Object>> values = new ArrayList<>();
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                List<Object> rowList = new ArrayList<>();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    rowList.add(cell.toString());
                }
                values.add(rowList);
            }

            try {
                if (values.isEmpty() || values.get(0) == null)
                    throw new IllegalStateException("Spreadsheet " + sheet.getSheetName() + " has no header row. Needs to start on top left.");
                MappingResult result = mappingUtil.mapSheet(values, sheet.getSheetName(), index); // TODO ask what id should be put here
                index++;
                if (!result.getStationDtos().isEmpty())
                    dtos.addAll(result.getStationDtos());
                if (result.getDataType() != null) {
                    types.add(result.getDataType());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
        if (!dtos.isEmpty() && !types.isEmpty()){
            DataMapDto<? extends RecordDtoImpl> dto = new DataMapDto<RecordDtoImpl>();
            logger.debug("Connect datatypes with stations through record");
            for (DataTypeWrapperDto typeDto : types) {
                SimpleRecordDto simpleRecordDto = new SimpleRecordDto(new Date().getTime(), typeDto.getSheetName(), 0);
                logger.trace("Connect"+dtos.get(0).getId()+"with"+typeDto.getType().getName());
                dto.addRecord(dtos.get(0).getId(), typeDto.getType().getName(), simpleRecordDto);
            }
            odhClient.pushData(dto);
        }
        logger.info("Data syncronization completed");
    }

}

