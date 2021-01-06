package it.bz.odh.spreadsheets.utils;


import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import it.bz.odh.spreadsheets.services.GraphApiAuthenticator;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


@Service
public class GraphDataFetcher {

    @Autowired
    private GraphApiAuthenticator graphApiAuthenticator;

    @Autowired
    private GraphDataMapper graphDataMapper;

    private String token;


//    @PostConstruct
//    public void posContruct() throws Exception {
//        // Commented out to be bae to test O-Auth without having to set up al the rest
////        String token = getToken();
////        System.out.println("TOKEN: " + token);
//    }


    private String getUser(String accessToken) throws IOException {
        URL url = new URL("https://graph.microsoft.com/v1.0/users");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
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

    private String makeDriveRequest(String accessToken, String userId) throws IOException {
        URL url = new URL("https://graph.microsoft.com/v1.0/users/" + userId + "/drive/root/children");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
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

    public void fetchSheet() throws Exception {
        checkToken();
        String downloadLink = getDownloadLink();
        FileUtils.copyURLToFile(
                new URL(downloadLink),
                new File("Book.xlsx"),
                10000,
                10000);
    }

    /**
     * Gets the token if not present yet or expired
     *
     * @return true if new token created
     * @throws Exception
     */
    private boolean checkToken() throws Exception {
        if (tokenExpireDate == null || tokenExpireDate.before(new Date())) {
            IAuthenticationResult result = graphApiAuthenticator.getAccessTokenByClientCredentialGrant();
            token = result.accessToken();
            tokenExpireDate = result.expiresOnDate();
            return true;
        }
        return false;
    }

//    public String getItemId(String token) throws Exception {
//        String userId = getUserId(getUser(token));
//        String driveRequest = makeDriveRequest(token, userId);
//        return graphDataMapper.getItemId(driveRequest);
//    }

//    public String getUserId(String token) throws Exception {
//        return graphDataMapper.getUserId(token);
//    }

    private String getDownloadLink() throws Exception {
        String user = getUser(token);
        String userId = graphDataMapper.getUserId(user);
        String driveRequest = makeDriveRequest(token, userId);
        return graphDataMapper.getDownloadLink(driveRequest);
    }
}