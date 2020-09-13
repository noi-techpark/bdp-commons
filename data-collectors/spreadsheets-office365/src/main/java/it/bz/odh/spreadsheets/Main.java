package it.bz.odh.spreadsheets;


import it.bz.odh.spreadsheets.services.GraphApiAuthenticator;
import it.bz.odh.spreadsheets.utils.GraphDataFetcher;
import it.bz.odh.spreadsheets.utils.GraphDataMapper;
import it.bz.odh.spreadsheets.utils.XLSXReader;

//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//@EnableScheduling
public class Main {
    public static void main(String[] args) throws Exception {


        String token = GraphApiAuthenticator.getAccessTokenByClientCredentialGrant().accessToken();

        String user = GraphDataFetcher.getUser(token);
        String userId = GraphDataMapper.getUserId(user);

        String driveIdResponse = GraphDataFetcher.getDriveId(token, userId);
        String downloadLink = GraphDataMapper.getDownloadLink(driveIdResponse);

        GraphDataFetcher.fetchSheet(downloadLink);

        XLSXReader.readSheet();
    }
}
