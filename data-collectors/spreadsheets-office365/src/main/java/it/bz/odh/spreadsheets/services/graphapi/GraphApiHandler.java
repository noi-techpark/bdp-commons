package it.bz.odh.spreadsheets.services.graphapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
 * Class to make the Graph requests to get the Download link of the Office 365 Workbook saved on Sharepoint
 */
@Service
public class GraphApiHandler {

    private static final Logger logger = LoggerFactory.getLogger(GraphApiHandler.class);

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private GraphApiAuthenticator graphApiAuthenticator;

    @Value("${sharepoint.host}")
    private String sharePointHost;

    @Value("${sharepoint.site-id}")
    private String siteId;

    @Value("${sharepoint.path-to-doc}")
    private String pathToDoc;

    // to be able to only write values, if sheet changed after last writing
    private Date lastChangeDate;

    private URL timeLastModified;
    private URL downloadSpreadsheet;

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
     * @return null if no changes where made, else it returns a XSSFWorkbook
     */
    public Workbook checkSpreadsheet() throws Exception {

        String token = graphApiAuthenticator.getToken();

        String timeLastModifiedString = getTimeLastModified(token);

        final Date timeLastModified = convertMicrosoftDateToJavaDate(timeLastModifiedString);

        if (lastChangeDate == null || lastChangeDate.before(timeLastModified)) {
            // extract download link link from JSON
            lastChangeDate = timeLastModified;
            logger.info("Downloading sheet...");
            Workbook spreadsheet = getSpreadsheet(token);
            logger.info("Downloading sheet done");
            return spreadsheet;
        }
        return null;
    }


    private Workbook getSpreadsheet(String token) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) downloadSpreadsheet.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);

        int httpResponseCode = conn.getResponseCode();
        if (httpResponseCode == HTTPResponse.SC_OK) {
            Workbook workbook = WorkbookFactory.create(conn.getInputStream());
            return workbook;
        } else {
            // TODO log that downloading failed
            return null;
        }
    }


    private String getTimeLastModified(String token) throws IOException {

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
            return response.toString();
        } else {
            return String.format("Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
        }
    }

    private Date convertMicrosoftDateToJavaDate(String date) throws ParseException {
        // Java's Instant could be used, but for now date is preferred for here https://stackoverflow.com/questions/48594916/convert-2018-02-02t065457-744z-string-to-date-in-android
        date = date.substring(0, date.length() - 1).replace("T", " ");
        Date javaDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        return javaDate;
    }


}
