package it.bz.odh.spreadsheets.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

/**
 * To fetch files from Sharepoint.
 */
public class FileUtil {


    @Value("${sharepoint.host}")
    private String sharePointHost;

    @Value("${sharepoint.site-id}")
    private String siteId;


    @Value("${sharepoint.path-to-files}")
    private String pathToFiles;

    private URL pathToImage;

    @PostConstruct
    private void postConstruct() throws MalformedURLException, URISyntaxException {

        pathToImage = new URL("https://" + sharePointHost + "/sites/" + siteId + "/_api/web/getfilebyserverrelativeurl('/sites/" + siteId + "/Shared%20Documents/" + pathToDoc + "')/$value");

        //Test that both URLs are valid
        pathToImage.toURI(); // does the extra checking required for validation of URI
    }


    public static File fetchFile(String token, String path, String name) {

        File fetchedFile = null;

        return convertImage(fetchedFile, name);
    }
}
