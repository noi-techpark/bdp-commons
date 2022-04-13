package it.bz.idm.bdp.augeg4.fun.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class CsvUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CsvUtils.class.getName());

    public static Iterable<CSVRecord> readCsvFileFromResources (String resourceFilePath) {
        InputStream inputStream = CsvUtils.class.getResourceAsStream(resourceFilePath);
        if (inputStream == null) {
            LOG.error("readCsvFileFromResources() failed: file not found");
            throw new IllegalStateException("can't find " + resourceFilePath);
        }
        try {
            Reader reader = new InputStreamReader(inputStream);
            return CSVFormat.EXCEL.withHeader().parse(reader);
        } catch (IOException e) {
            LOG.error("readCsvFileFromResources() failed: {}", e.getMessage());
            throw new IllegalStateException("can't read " + resourceFilePath, e);
        }
    }

}
