package it.bz.idm.bdp.spreadsheets;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.RepeatCellRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Response;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

@Component
public class GoogleSpreadSheetDataFetcher implements DataFetcher{

	@Value("classpath:/META-INF/spring/client_secret.json")
	private Resource clientSecret;

	@Value("${spreadsheetId}")
	private String spreadhSheetId;

    Sheets service;
    NetHttpTransport HTTP_TRANSPORT ;
    JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${credentialsFolder}")
    private String CREDENTIALS_FOLDER;

    @Value("${spreadsheet.range}")
	private String spreadsheetRange;

    private Credential getCredentials() throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(clientSecret.getInputStream()));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singleton(SheetsScopes.SPREADSHEETS))

        		.setDataStoreFactory(new FileDataStoreFactory(new File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
	@Override
	public void authenticate() {
    	try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	    	service = new Sheets.Builder(HTTP_TRANSPORT,JSON_FACTORY,getCredentials()).build();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public Object fetchSheet() {
		try {
			Spreadsheet sheet = service.spreadsheets().get(spreadhSheetId).execute();
			return sheet;
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
	/**
	 * @param missingPositions rows in which address or (longituted and longitude information is missing)
	 * @param collumnPosition column which will be marked as wrong wit a red background
	 */
	public void markMissing(Set<Integer> missingPositions, Integer collumnPosition, Integer sheetId) {
		List<Request> list = new ArrayList<>();
		Color backgroundColor = new Color();
		backgroundColor.setBlue(0f);
		backgroundColor.setGreen(0f);
		backgroundColor.setRed(1f);
		Color white = new Color();
		white.setGreen(1f);
		white.setBlue(1f);
		white.setRed(1f);
		Request clearRequest = new Request().setRepeatCell(new RepeatCellRequest()
				.setCell(new CellData().setUserEnteredFormat(new CellFormat().setBackgroundColor(white)))
				.setRange(new GridRange().setSheetId(sheetId)
						.setStartRowIndex(1)
						.setStartColumnIndex(collumnPosition).setEndColumnIndex(collumnPosition+1))
				.setFields("userEnteredFormat.backgroundColor"));
		list.add(clearRequest);
		for (Integer missing : missingPositions) {
			Request colorRequest = new Request().setRepeatCell(new RepeatCellRequest()
					.setCell(new CellData().setUserEnteredFormat(new CellFormat().setBackgroundColor(backgroundColor)))
					.setRange(new GridRange().setSheetId(sheetId)
							.setStartRowIndex(missing).setEndRowIndex(missing+1)
							.setStartColumnIndex(collumnPosition).setEndColumnIndex(collumnPosition+1))
					.setFields("userEnteredFormat.backgroundColor"));
			list.add(colorRequest);
		}
		BatchUpdateSpreadsheetRequest requests = new BatchUpdateSpreadsheetRequest().setRequests(list);
		try {
			BatchUpdateSpreadsheetResponse execute = service.spreadsheets().batchUpdate(spreadhSheetId, requests).execute();
			List<Response> replies = execute.getReplies();
			replies.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
