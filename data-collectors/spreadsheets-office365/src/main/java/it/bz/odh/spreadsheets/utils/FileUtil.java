package it.bz.odh.spreadsheets.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.annotation.PostConstruct;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.bz.odh.spreadsheets.services.microsoft.AuthTokenGenerator;

/**
 * To fetch files from Sharepoint.
 */
@Component
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @Value("${sharepoint.host}")
    private String sharePointHost;

    @Value("${sharepoint.site-id}")
    private String siteId;

    @Value("${sharepoint.path-to-files}")
    private String pathToFiles;

    private URL pathToImage;

    @PostConstruct
    private void postConstruct() throws Exception {

        // test fetching file

        pathToImage = new URL(
            "https://" + sharePointHost + "/sites/" + siteId + "/_api/web/getfilebyserverrelativeurl('/sites/"
                    + siteId + "/Shared%20Documents/" + pathToFiles + "/eurac_terraxcube_logo.eps')/TimeLastModified/$value");

        logger.info("Fetching workbook...");

        String token = authTokenGenerator.getToken();

        HttpURLConnection conn = (HttpURLConnection) pathToImage.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);

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
        } else {
            String errorMessage = String.format("Fetching workbook failed. Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
            logger.error(errorMessage);
            throw new IOException(errorMessage);
        }
    }

    public File fetchFile(String path, String name) throws Exception {

        String token = authTokenGenerator.getToken();

        pathToImage = new URL(
                "https://" + sharePointHost + "/sites/" + siteId + "/_api/web/getfilebyserverrelativeurl('/sites/"
                        + siteId + "/Shared%20Documents/" + pathToFiles + "')/$value");

        // Test that both URLs are valid
        pathToImage.toURI(); // does the extra checking required for validation of URI

        File fetchedFile = null;

        return fetchedFile;
    }
}
