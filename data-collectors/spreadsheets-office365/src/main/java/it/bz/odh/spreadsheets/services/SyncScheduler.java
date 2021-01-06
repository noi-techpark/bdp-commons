package it.bz.odh.spreadsheets.services;

import it.bz.idm.bdp.dto.*;
import it.bz.odh.spreadsheets.dto.DataTypeWrapperDto;
import it.bz.odh.spreadsheets.dto.MappingResult;
import it.bz.odh.spreadsheets.utils.DataMappingUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SyncScheduler.class);
    @Value("${graph.sheetName}")
    private String sheetName;
    private Function<DataTypeWrapperDto, DataTypeDto> mapper = (dto) -> {
        return dto.getType();
    };

    @Autowired
    private GraphDataFetcher graphDataFetcher;

//    @Autowired
//    private GraphChangeNotificationClient graphChangeNotificationClient;

    @Lazy
    @Autowired
    private ODHClient odhClient;

    @Autowired
    private GraphApiAuthenticator graphApiAuthenticator;

//    @PostConstruct
//    private void postConstruct() throws Exception {
//        String token = graphDataFetcher.getToken();
//        graphChangeNotificationClient.makeSubscription(token);
//    }


    @Autowired
    private DataMappingUtil mappingUtil;

    //    @Scheduled(cron = "${cron.fetch}")
    @Scheduled(cron = "${cron}")
    public void fetchSheet() throws Exception {
        logger.debug("Cron job manual sync started");
        if (graphDataFetcher.fetchSheet()) {
            logger.debug("Syncing data with BDP");
            syncData();
            logger.debug("Done: Syncing data with BDP");
        } else
            logger.debug("No new changes detected, skip sync with BDP");

        logger.debug("Cron job manual sync end");
    }

    //    @Scheduled(cron = "${cron.subscription}")
    public void renewSubscription() throws Exception {
        logger.debug("Fetch sheet started");
        logger.debug("Fetch sheet done");
    }

    /**
     * Fetches the worksheet from Office365 and converts the data to BDP format
     * Then it writes the values to the BDP
     *
     * @throws Exception
     */
    private void syncData() throws Exception {
        // fetch sheet
        //read from disk
        XSSFWorkbook workbook = readSheetFromDisk();

        // iterate over values and
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        StationList dtos = new StationList();
        List<DataTypeWrapperDto> types = new ArrayList<DataTypeWrapperDto>();
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
                    rowList.add(cell);
                }
                values.add(rowList);
            }


            try {

                if (values.isEmpty() || values.get(0) == null)
                    throw new IllegalStateException("Spreadsheet " + sheet.getSheetName() + " has no header row. Needs to start on top left.");
                MappingResult result = mappingUtil.mapSheet(values, sheet.getSheetName(), index);
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
        if (!dtos.isEmpty())
            odhClient.syncStations(dtos);
        if (!types.isEmpty()) {
            List<DataTypeDto> dTypes = types.stream().map(mapper).collect(Collectors.toList());
            odhClient.syncDataTypes(dTypes);
        }
        DataMapDto<? extends RecordDtoImpl> dto = new DataMapDto<RecordDtoImpl>();
        for (DataTypeWrapperDto typeDto : types) {
            SimpleRecordDto simpleRecordDto = new SimpleRecordDto(new Date().getTime(), typeDto.getSheetName(), 0);
            dto.addRecord(dtos.get(0).getId(), typeDto.getType().getName(), simpleRecordDto);
        }
        odhClient.pushData(dto);
    }


    public XSSFWorkbook readSheetFromDisk() throws IOException {
        File file = new File(sheetName);

        FileInputStream fis = new FileInputStream(file);

        // we create an XSSF Workbook object for our XLSX Excel File
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        return workbook;
    }
}

