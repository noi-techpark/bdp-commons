package it.bz.odh.spreadsheets.utils;

import it.bz.odh.spreadsheets.services.SyncScheduler;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Component
public class XLSXReader {

    private static final Logger logger = LoggerFactory.getLogger(SyncScheduler.class);


//    public void readSheet() throws IOException {
//        File file = new File("Book.xlsx");
//
//        FileInputStream fis = new FileInputStream(file);
//
//        // we create an XSSF Workbook object for our XLSX Excel File
//        XSSFWorkbook workbook = new XSSFWorkbook(fis);
//
//
//        /**
//         * if SINGLE_SHEET_NAMES is defined in application.properties print only selected sheets,
//         * else print all sheets
//         */
//        if (singleSheetNames.size() > 0 && singleSheetNames.get(0).length() > 0){
//            for (String singleSheetName:singleSheetNames){
//                XSSFSheet sheet = workbook.getSheet(singleSheetName);
//                if (sheet == null){
//                  logger.error("No sheet with name " + singleSheetName + " found!");
//                }else{
//                    Iterator<Row> rowIt = sheet.iterator();
//
//                    //print sheet to console
//                    while(rowIt.hasNext()) {
//                        Row row = rowIt.next();
//                        // iterate on cells for the current row
//                        Iterator<Cell> cellIterator = row.cellIterator();
//
//                        while (cellIterator.hasNext()) {
//                            Cell cell = cellIterator.next();
//                            System.out.print(cell.toString() + ";");
//                        }
//                        System.out.println();
//                    }
//                }
//            }
//        } else {
//            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
//
//            while (sheetIterator.hasNext()) {
//                Sheet sheet = sheetIterator.next();
//                // we iterate on rows
//                Iterator<Row> rowIt = sheet.iterator();
//
//                while (rowIt.hasNext()) {
//                    Row row = rowIt.next();
//
//                    // iterate on cells for the current row
//                    Iterator<Cell> cellIterator = row.cellIterator();
//
//                    while (cellIterator.hasNext()) {
//                        Cell cell = cellIterator.next();
//                        System.out.print(cell.toString() + ";");
//                    }
//
//                    System.out.println();
//                }
//            }
//        }
//
//        workbook.close();
//        fis.close();
//    }

    public XSSFWorkbook getSheet() throws IOException {
        File file = new File("Book.xlsx");

        FileInputStream fis = new FileInputStream(file);

        // we create an XSSF Workbook object for our XLSX Excel File
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        return workbook;
    }
}
