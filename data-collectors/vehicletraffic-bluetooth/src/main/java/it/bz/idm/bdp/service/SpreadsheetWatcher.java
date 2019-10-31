package it.bz.idm.bdp.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Channel;

import it.bz.idm.bdp.util.GoogleAuthenticator;

@Lazy
@Service
public class SpreadsheetWatcher extends GoogleAuthenticator {

	private Drive service;

	@Value("${spreadsheetId}")
	private String spreadsheetId;

	@Scheduled(fixedDelay = 1000 * 60 * 55, initialDelay = 1000)
	public void watchBluetoothBoxesSpreadsheet() {
		Channel channel = new Channel();
		channel.setId(UUID.randomUUID().toString());
		channel.setType("web_hook");
		channel.setAddress("https://boxes.opendatahub.bz.it/dc-vehicletraffic-bluetooth/trigger");
		registerWatch(channel);
	}

	public void registerWatch(Channel c) {
		try {
			service.files().watch(spreadsheetId, c).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initGoogleClient(NetHttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY, Credential credential)
			throws IOException {
		service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName("google spreadsheet collector").build();
	}
}
