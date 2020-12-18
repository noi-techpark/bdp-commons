package it.bz.odh.spreadsheets.services;

import it.bz.idm.bdp.dto.*;
import it.bz.odh.spreadsheets.dto.DataTypeWrapperDto;
import it.bz.odh.spreadsheets.dto.MappingResult;
import it.bz.odh.spreadsheets.utils.DataMappingUtil;
import it.bz.odh.spreadsheets.utils.GraphDataFetcher;
import it.bz.odh.spreadsheets.utils.XLSXReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SyncScheduler.class);

    @Autowired
    private GraphDataFetcher graphDataFetcher;


//    @Lazy
    @Autowired
    private ODHClient odhClient;

//    @Autowired
//    private GraphChangeNotificationClient graphChangeNotificationClient;


    @Autowired
    private GraphApiAuthenticator graphApiAuthenticator;

    @Autowired
    private XLSXReader xlsxReader;

    @Autowired
    private DataMappingUtil mappingUtil;

//    @PostConstruct
//    public void postConstruct() throws Exception {
//        String token = graphDataFetcher.getToken();
//        graphChangeNotificationClient.makeSubscription(token);
//    }

//    @Scheduled(cron = "${cron.fetch}")
    @Scheduled(cron = "${cron}")
    public void fetchSheet() throws Exception {
        logger.debug("Fetch sheet started");

        // Commented out to be bae to test O-Auth without having to set up al the rest

//        String token = graphDataFetcher.getToken();
//        logger.debug("TOKEN: " + token);
//        String itemId = graphDataFetcher.getItemId(token);
//        graphDataFetcher.fetchSheet(token);
//        graphDataFetcher.get
//        graphChangeNotificationClient.makeSubscription(token);
//        syncData(token);
        logger.debug("Fetch sheet done");
    }

//    @Scheduled(cron = "${cron.subscription}")
    public void renewSubscription() throws Exception {
        logger.debug("Fetch sheet started");
        logger.debug("Fetch sheet done");
    }


    /**
     * scheduled job which syncs odh with the spreadsheet
     */
    private void syncData(String token) throws Exception {
        graphDataFetcher.fetchSheet(token);
        XSSFWorkbook workbook = xlsxReader.getSheet();
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        StationList dtos = new StationList();
        List<DataTypeWrapperDto> types = new ArrayList<DataTypeWrapperDto>();
        int index = 0;
        while (sheetIterator.hasNext()){
            Sheet sheet = sheetIterator.next();

            //convert values of sheet to List<List<Object>> to be able to map data with data-mapping of dc-googlespreadsheets
            List<List<Object>> values = new ArrayList<>();
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()){
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                List<Object> rowList = new ArrayList<>();
                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    rowList.add(cell);
                }
                values.add(rowList);
            }


            try {

                if (values.isEmpty() || values.get(0) == null)
                    throw new IllegalStateException("Spreadsheet "+sheet.getSheetName()+" has no header row. Needs to start on top left.");
                MappingResult result = mappingUtil.mapSheet(values,sheet.getSheetName(),index);
                index++;
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

