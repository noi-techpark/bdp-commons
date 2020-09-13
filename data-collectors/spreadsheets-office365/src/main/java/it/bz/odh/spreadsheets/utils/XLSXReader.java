package it.bz.odh.spreadsheets.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

@Component
public class XLSXReader {

    public static void readSheet() throws IOException {
        File file = new File("Book.xlsx");

        FileInputStream fis = new FileInputStream(file);

        // we create an XSSF Workbook object for our XLSX Excel File
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet buildingsSheet = workbook.getSheet("Buildings");
        XSSFSheet floorSheet = workbook.getSheet("Floors");
//        XSSFSheet  = workbook.getSheet("Floors");

        // we iterate on rows
        Iterator<Row> rowIt = buildingsSheet.iterator();

        while(rowIt.hasNext()) {
            Row row = rowIt.next();

            // iterate on cells for the current row
            Iterator<Cell> cellIterator = row.cellIterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                System.out.print(cell.toString() + ";");
            }

            System.out.println();
        }

        workbook.close();
        fis.close();
    }
}
