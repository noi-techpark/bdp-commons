package it.bz.odh.spreadsheets.services;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;

/**
 * Class that uses the @GraphApiAuthenticator to generate the token and then uses The @GraphDataMapper to extract the download link from the JSON.
 * <p>
 * The last change date gets compared, to see if changes where made since last fetch.
 * So the data gets written to the ODH only if changes in the sheet were made.
 * <p>
 * This class is used in the @SyncScheduler to automate the above mentioned.
 */
@Service
public class GraphDataFetcher {


    private static final Logger logger = LoggerFactory.getLogger(GraphDataFetcher.class);


    @Value("${graph.sheetName}")
    protected String sheetName;

    @Autowired
    private GraphApiAuthenticator graphApiAuthenticator;

    @Autowired
    private GraphDataMapper graphDataMapper;


    @PostConstruct
    private void posConstruct() throws Exception {

        //Check that sheetName property is set correct
        if (sheetName == null || sheetName.length() == 0)
            throw new InvalidConfigurationPropertyValueException("sheetName", sheetName, "sheetName must be set in .env file and can't be empty");
        if (!sheetName.contains(".xlsx") || sheetName.length() < 6)
            throw new InvalidConfigurationPropertyValueException("sheetName", sheetName, "sheetName must have correct format and end with: .xlsx");
    }


    /**
     * Checks token validity, then if sheet had changes it downloads the sheet in .xlsx format
     *
     * @return true if sheet was downloaded
     * @throws Exception
     */
    public boolean fetchSheet() throws Exception {
        logger.info("Check if changes in spreadsheet where made");

        String token = graphApiAuthenticator.checkToken();

        String downloadLink = graphDataMapper.getDownloadLink(token);

        // if download link is null, no changes detected
        if (downloadLink != null) {
            FileUtils.copyURLToFile(
                    new URL(downloadLink),
                    new File(sheetName));
            logger.info("Changes detected, downloading sheet");
            return true;
        } else
            logger.info("No changes detected, download skipped");

        return false;
    }

}