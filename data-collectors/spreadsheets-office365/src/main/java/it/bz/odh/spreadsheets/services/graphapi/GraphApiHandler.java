package it.bz.odh.spreadsheets.services.graphapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

    @Value("${graph.email}")
    private String mail;

    @Value("${graph.sheetName}")
    private String sheetName;

    // to be able to only write values, if sheet changed after last writing
    private Date lastChangeDate;

    @PostConstruct
    private void postConstruct() {
        // check properties, skip sheetName: gets checked in @GraphDataFetcher

        //Check that sheetName property is set correct
        if (mail == null || mail.length() == 0)
            throw new InvalidConfigurationPropertyValueException("mail", mail, "mail must be set in .env file and can't be empty");

        if (!mail.contains("@"))
            throw new InvalidConfigurationPropertyValueException("mail", mail, "mail must be a valid e-mail and contain a @");

    }

    /**
     * Returns the download link of the sheet
     * If no changes where made in the sheet since last check, null is returned
     *
     * @param token
     * @return the download link, null if no changes happened
     * @throws IOException
     */
    public String getDownloadLink(String token) throws IOException, ParseException {
        String downloadLink = null;

        logger.info("Getting download link from GraphAPI");

        String userId = getUserId(token);
        String driveResponse = makeDriveRequest(userId, token);

        // extract the download link from the drive request response
        JsonNode downloadLinkNode = mapper.readTree(driveResponse);
        if (downloadLinkNode.get("value").isArray()) {

            // since value is an array and second entry contains link and second entry contains
            // the last modify date, we need to split value
            JsonNode jsonNode = downloadLinkNode.get("value").get(1);

            // extract date and check if first time OR sheet had changes since last check

            // remove timezone, because java doesnt understand Microsoft's date format
            // so time zone needs to be removed and is not important, because only date delta is need, zone doesn't matter

            // Java's Instant could be used, but for now date is preferred for here https://stackoverflow.com/questions/48594916/convert-2018-02-02t065457-744z-string-to-date-in-android
            String graphDate = jsonNode.get("lastModifiedDateTime").asText();
            graphDate = graphDate.substring(0, graphDate.length() - 1).replace("T", " ");
            Date currentLastChangeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(graphDate);


            if (lastChangeDate == null || lastChangeDate.before(currentLastChangeDate)) {
                // extract download link link from JSON
                lastChangeDate = currentLastChangeDate;
                logger.info("Extracting download link");
                if (sheetName.equals(jsonNode.get("name").asText().replace("\"", ""))) {
                    downloadLink = jsonNode.get("@microsoft.graph.downloadUrl").asText().replace("\"", "");
                }
            }
        }
        return downloadLink;
    }


    private String getUserId(String token) throws IOException {
        String userId = null;

        String users = getUsers(token);

        JsonNode downloadLinkNode = mapper.readTree(users);
        if (downloadLinkNode.get("value").isArray()) {
            for (JsonNode jsonNode : downloadLinkNode.get("value")) {

                // extract user id from json
                if (mail.equals(jsonNode.get("mail").asText().replace("\"", ""))) {
                    userId = jsonNode.get("id").asText().replace("\"", "");
                }
            }
        }
        return userId;
    }


    private String getUsers(String token) throws IOException {
        URL url = new URL("https://graph.microsoft.com/v1.0/users");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Accept", "application/json");

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

    private String makeDriveRequest(String userId, String token) throws IOException {
        URL url = new URL("https://graph.microsoft.com/v1.0/users/" + userId + "/drive/root/children");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Accept", "application/json");

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
}
