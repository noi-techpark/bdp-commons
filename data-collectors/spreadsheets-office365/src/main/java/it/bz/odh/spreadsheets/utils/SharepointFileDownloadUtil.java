package it.bz.odh.spreadsheets.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.bz.odh.spreadsheets.services.microsoft.AuthTokenGenerator;

/**
 * To fetch files from a Sharepoint site
 */
@Component
public class SharepointFileDownloadUtil {

    private static final Logger logger = LoggerFactory.getLogger(SharepointFileDownloadUtil.class);

    @Autowired
    private AuthTokenGenerator authTokenGenerator;

    @Value("${sharepoint.host}")
    private String sharePointHost;

    @Value("${sharepoint.site-id}")
    private String siteId;

    @Value("${sharepoint.path-to-files}")
    private String pathToFiles;

    private URL pathToImage;


    public HttpURLConnection fetchFile(String name) throws Exception {

        String token = authTokenGenerator.getToken();

        pathToImage = new URL(
                "https://" + sharePointHost + "/sites/" + siteId + "/_api/web/getfilebyserverrelativeurl('/sites/"
                        + siteId + "/Shared%20Documents/" + pathToFiles + name + "')/$value");

        // Test that both URLs are valid
        pathToImage.toURI(); // does the extra checking required for validation of URI

        HttpURLConnection conn = (HttpURLConnection) pathToImage.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);

        int httpResponseCode = conn.getResponseCode();
        if (httpResponseCode == HTTPResponse.SC_OK) {
            logger.info("Fetching file done");
            return conn;

        } else {
            String errorMessage = String.format("Fetching workbook failed. Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
            logger.error(errorMessage);
            throw new IOException(errorMessage);
        }
    }
}
