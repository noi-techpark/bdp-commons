package it.bz.odh.spreadsheets.utils.microsoft;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import it.bz.odh.spreadsheets.services.microsoft.AuthTokenGenerator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Util to fetch a Excel Workbook from a Sharepoint Sites Shared Documents Folder.
 * It can also check the last time modified meta data, to see if changes where made since the last fetching.
 * <p>
 * The generated workbook is a Workbook from org.apache.poi poi-ooxml library.
 */
@Component
public class WorkbookUtil {

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @Value("${sharepoint.host}")
    private String sharePointHost;

    @Value("${sharepoint.site-id}")
    private String siteId;

    @Value("${sharepoint.path-to-doc}")
    private String pathToDoc;

    // to save lastChangeDate last fetch
    private Date lastChangeDate;

    private URL timeLastModified;
    private URL downloadSpreadsheet;

    private static final Logger logger = LoggerFactory.getLogger(WorkbookUtil.class);

    // https://stackoverflow.com/questions/48594916/convert-2018-02-02t065457-744z-string-to-date-in-android
    SimpleDateFormat microsoftDateConverter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @PostConstruct
    private void postConstruct() throws MalformedURLException, URISyntaxException {

        downloadSpreadsheet = new URL("https://" + sharePointHost + "/sites/" + siteId + "/_api/web/getfilebyserverrelativeurl('/sites/" + siteId + "/Shared%20Documents/" + pathToDoc + "')/$value");
        timeLastModified = new URL("https://" + sharePointHost + "/sites/" + siteId + "/_api/web/getfilebyserverrelativeurl('/sites/" + siteId + "/Shared%20Documents/" + pathToDoc + "')/TimeLastModified/$value");

        //Check that both URLs are valid
        downloadSpreadsheet.toURI(); // does the extra checking required for validation of URI
        timeLastModified.toURI(); // does the extra checking required for validation of URI

    }

    /**
     * Checks if changes where made in the Spreadsheet
     *
     * @return null if no changes where made, else it returns the Workbook
     */
    public Workbook checkSpreadsheet() throws Exception {

        String token = authTokenGenerator.getToken();

        final Date timeLastModified = getTimeLastModified(token);

        if (lastChangeDate == null || lastChangeDate.before(timeLastModified)) {

            lastChangeDate = timeLastModified;

            return getWorkbook(token);
        }

        return null;
    }


    private Workbook getWorkbook(String token) throws IOException {
        logger.info("Fetching workbook...");

        HttpURLConnection conn = (HttpURLConnection) downloadSpreadsheet.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);

        int httpResponseCode = conn.getResponseCode();
        if (httpResponseCode == HTTPResponse.SC_OK) {
            Workbook workbook = WorkbookFactory.create(conn.getInputStream());
            logger.info("Fetching workbook done");
            return workbook;
        } else {
            String errorMessage = String.format("Fetching workbook failed. Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
            logger.error(errorMessage);
            throw new IOException(errorMessage);
        }
    }


    private Date getTimeLastModified(String token) throws IOException, ParseException {
        logger.info("Getting TimeLastModified...");

        HttpURLConnection conn = (HttpURLConnection) timeLastModified.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("odata", "verbose");

        int httpResponseCode = conn.getResponseCode();
        if (httpResponseCode == HTTPResponse.SC_OK) {

            StringBuilder response;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {

                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            String dateTime = response.toString();
            logger.info("Getting TimeLastModified done: " + dateTime);
            return microsoftDateConverter.parse(dateTime);
        } else {
            String errorMessage = String.format("Get TimeLastModified failed. Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
            logger.error(errorMessage);
            throw new IOException(errorMessage);
        }
    }



}
