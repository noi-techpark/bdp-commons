package it.bz.idm.bdp.dconstreetparkingbz;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import com.google.api.services.sheets.v4.model.ValueRange;

import it.bz.idm.bdp.dto.StationList;

@Lazy
@Component
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class OnstreetParkingBzDataRetriever {

    private static final Logger LOG = LoggerFactory.getLogger(OnstreetParkingBzDataRetriever.class.getName());

    @Value("${spreadsheet.sheetName}")
    private String SHEETNAME;

    @Autowired
    private OnstreetParkingBzDataConverter converter;

    @Lazy
    @Autowired
    private OnstreetParkingBzSpreadsheetReader sheetReader;

    public OnstreetParkingBzDataRetriever() {
        LOG.debug("Create instance");
    }

    @PostConstruct
    private void initClient() {
        LOG.debug("Init");
    }

    /**
     * Fetch anagrafic data from a Google Spreadsheet containing all stations (ParkingSensors).
     * 
     * @return
     * @throws Exception
     */
    public StationList fetchStations() throws Exception {
        String methodName = "fetchStations";
        LOG.info("START."+methodName);
        StationList retval = new StationList();
        try {
            StringBuffer err = new StringBuffer();

            //read data from Google Spreadsheet
            ValueRange valueRange = sheetReader.getWholeSheet(SHEETNAME);

            //Convert to internal representation
            retval = converter.convertSheetValueRangeToStationList(valueRange);

            if ( (retval==null || retval.size()<=0) && err.length()>0 ) {
                throw new RuntimeException("NO DATA FETCHED: "+err);
            }
        } catch (Exception ex) {
            LOG.error("ERROR in "+methodName+": " + ex.getMessage(), ex);
            throw ex;
        }
        LOG.info("END."+methodName);
        return retval;
    }

}
