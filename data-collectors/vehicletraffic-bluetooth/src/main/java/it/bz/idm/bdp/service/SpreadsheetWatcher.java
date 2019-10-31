package it.bz.idm.bdp.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Channel;

import it.bz.idm.bdp.util.GoogleAuthenticator;

@Service
public class SpreadsheetWatcher extends GoogleAuthenticator{

    private Drive service;

    @Value("${spreadsheetId}")
    private String spreadsheetId;

    public void registerWatch(Channel c) throws IOException, GeneralSecurityException {
        service.files().watch(spreadsheetId, c).execute();
    }

	@Override
	public void initGoogleClient(NetHttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY, Credential credential)
			throws IOException {
		service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("google spreadsheet collector").build();
	}
}
