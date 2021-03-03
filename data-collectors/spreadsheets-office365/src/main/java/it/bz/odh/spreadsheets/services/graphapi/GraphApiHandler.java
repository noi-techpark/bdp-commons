package it.bz.odh.spreadsheets.services.graphapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    @Value("${sharepoint.timeLastModifiedUrl}")
    private String modifiedUrl;

    @Value("${sharepoint.fetchUrl}")
    private String fetchUrl;

    // to be able to only write values, if sheet changed after last writing
    private Date lastChangeDate;

    @PostConstruct
    private void postConstruct() throws MalformedURLException, URISyntaxException {

        //Check that both URLs are valid
        URL modifiedUrlTest = new URL(modifiedUrl); // this would check for the protocol
        modifiedUrlTest.toURI(); // does the extra checking required for validation of URI

        URL fetchUrlTest = new URL(fetchUrl); // this would check for the protocol
        fetchUrlTest.toURI(); // does the extra checking required for validation of URI

//        checkSpreadsheet();
    }

    /**
     * Checks if changes where made in the Spreadsheet
     *
     * @return null if no changes where made, else it returns a XSSFWorkbook
     */
    public XSSFWorkbook checkSpreadsheet() throws Exception {

        String token = graphApiAuthenticator.getToken();

        String timeLastModifiedString = getTimeLastModified(token);

        final Date timeLastModified = convertMicrosoftDateToJavaDate(timeLastModifiedString);

        if (lastChangeDate == null || lastChangeDate.before(timeLastModified)) {
            // extract download link link from JSON
            lastChangeDate = timeLastModified;
            logger.info("Downloading sheet...");
            XSSFWorkbook spreadsheet = getSpreadsheet(token);
            logger.info("Downloading sheet done");
            return  spreadsheet;
        }
        return null;
    }


    private XSSFWorkbook getSpreadsheet(String token) throws IOException {
        URL url = new URL(fetchUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);

        int httpResponseCode = conn.getResponseCode();
        if (httpResponseCode == HTTPResponse.SC_OK) {
            return new XSSFWorkbook(conn.getInputStream());
        } else {
            // TODO log that downloading failed
            return null;
        }
    }


    private String getTimeLastModified(String token) throws IOException {
        URL url = new URL(modifiedUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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
