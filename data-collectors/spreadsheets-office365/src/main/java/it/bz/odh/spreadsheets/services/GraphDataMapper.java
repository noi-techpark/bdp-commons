package it.bz.odh.spreadsheets.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * To map the user and sheet data to a JSON Node and process data to fetch the spreadsheeet
 */
@Service
public class GraphDataMapper {

    @Value("${graph.email}")
    private String mail;

    @Value("${graph.sheetName}")
    private String sheetName;

    private static ObjectMapper mapper = new ObjectMapper();


    public String  getUserId(String user) throws IOException {
        String userId = null;

        // throw exception, if userId not found
        JsonNode downloadLinkNode = mapper.readTree(user);
        if (downloadLinkNode.get("value").isArray()) {
            for (JsonNode jsonNode : downloadLinkNode.get("value")) {
                if(mail.equals(jsonNode.get("mail").asText().replace("\"",""))){
                    userId = jsonNode.get("id").asText().replace("\"","");
                }
            }
        }
        return userId;
    }

    public String  getItemId(String driveIdResponse) throws IOException {
        String itemId = null;
        JsonNode downloadLinkNode = mapper.readTree(driveIdResponse);
        if(downloadLinkNode.get("value").isArray()) {
            for (JsonNode jsonNode : downloadLinkNode.get("value")) {
                if(sheetName.equals(jsonNode.get("name").asText().replace("\"",""))){
                    itemId = jsonNode.get("@microsoft.graph.downloadUrl").asText().replace("\"","");
                }
            }
        }
        return itemId;
    }

    public String getDownloadLink(String driveIdResponse) throws IOException {
        String downloadLink = null;
        JsonNode downloadLinkNode = mapper.readTree(driveIdResponse);
        if(downloadLinkNode.get("value").isArray()) {
            for (JsonNode jsonNode : downloadLinkNode.get("value")) {
                if(sheetName.equals(jsonNode.get("name").asText().replace("\"",""))){
                    downloadLink = jsonNode.get("@microsoft.graph.downloadUrl").asText().replace("\"","");
                }
            }
        }
        return downloadLink;
    }
}
