// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.spreadsheets.utils;

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


    // to save lastChangeDate of last fetch
    private Date lastChangeDate;

    private URL timeLastModifiedURL;
    private URL downloadWorkbookURL;

    private static final Logger logger = LoggerFactory.getLogger(WorkbookUtil.class);

    @Autowired
    private SharepointDateUtil dateUtil;

    @PostConstruct
    private void postConstruct() throws MalformedURLException, URISyntaxException {

        downloadWorkbookURL = new URL("https://" + sharePointHost + "/sites/" + siteId + "/_api/web/getfilebyserverrelativeurl('/sites/" + siteId + "/Shared%20Documents/" + pathToDoc + "')/$value");
        timeLastModifiedURL = new URL("https://" + sharePointHost + "/sites/" + siteId + "/_api/web/getfilebyserverrelativeurl('/sites/" + siteId + "/Shared%20Documents/" + pathToDoc + "')/TimeLastModified/$value");

        //Test that both URLs are valid
        downloadWorkbookURL.toURI(); // does the extra checking required for validation of URI
        timeLastModifiedURL.toURI(); // does the extra checking required for validation of URI
    }

    /**
     * Checks for changes in the Workbook
     * and returns it, if changes where made
     * since the last time checked.
     *
     *
     * @return the Workbook if changes where made, else null
     */
    public Workbook checkWorkbook() throws Exception {

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

        HttpURLConnection conn = (HttpURLConnection) downloadWorkbookURL.openConnection();

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

        HttpURLConnection conn = (HttpURLConnection) timeLastModifiedURL.openConnection();

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
            logger.info("Getting TimeLastModified done: {}", dateTime);
            return dateUtil.parseDate(dateTime);
        } else {
            String errorMessage = String.format("Get TimeLastModified failed. Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
            logger.error(errorMessage);
            throw new IOException(errorMessage);
        }
    }



}
