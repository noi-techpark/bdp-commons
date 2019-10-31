package it.bz.idm.bdp.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import it.bz.idm.bdp.util.GoogleAuthenticator;

@Component
public class SpreadsheetReader extends GoogleAuthenticator {

    @Value("${spreadsheetId}")
    private String spreadhSheetId;

    @Value("${spreadsheet.range}")
	private String spreadsheetRange;

    private Sheets service;

	public Spreadsheet fetchSheet() {
		try {
			return service.spreadsheets().get(spreadhSheetId).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public ValueRange getWholeSheet(String sheedTitle) {
		return getValues(sheedTitle+"!"+ spreadsheetRange);
	}
	
	
	private ValueRange getValues(String range) {
		try {
			return service.spreadsheets().values().get(spreadhSheetId, range).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void initGoogleClient(NetHttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY, Credential credential)
			throws IOException {
		service = new Sheets.Builder(HTTP_TRANSPORT,JSON_FACTORY,credential).build();
	}


}
