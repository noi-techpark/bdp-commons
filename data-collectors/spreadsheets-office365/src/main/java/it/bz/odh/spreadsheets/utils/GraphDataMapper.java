package it.bz.odh.spreadsheets.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * To map the user and sheet data to a JSON Node and process data to fetch the spreadsheeet
 */
public class GraphDataMapper {


    private static ObjectMapper mapper = new ObjectMapper();


    public static String getUserId(String user) throws IOException {
        String userId = null;

        //TODO check for user in application.properties to fetch correct id, if more users present
        JsonNode downloadLinkNode = mapper.readTree(user);
        JsonNode value = downloadLinkNode.get("value").get(0).get("id");
        System.out.println(value.toString().replace("\"",""));
        userId = value.toString().replace("\"","");
        return userId;
    }

    public static String getDownloadLink(String driveIdResponse) throws IOException {
        //TODO check for file name in application.properties to fetch correct id, if more files present
        JsonNode downloadLinkNode = mapper.readTree(driveIdResponse);
        JsonNode value = downloadLinkNode.get("value").get(0).get("@microsoft.graph.downloadUrl");
        return value.toString().replace("\"","");
    }
}
