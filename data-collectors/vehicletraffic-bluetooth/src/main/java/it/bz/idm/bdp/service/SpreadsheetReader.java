package it.bz.idm.bdp.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;

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
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

@Component
public class SpreadsheetReader {

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
    
    @PostConstruct
    private void init() {
    	this.authenticate();
    }
    

    private Credential getCredentials() throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(clientSecret.getInputStream()));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singleton(SheetsScopes.SPREADSHEETS))

        		.setDataStoreFactory(new FileDataStoreFactory(new File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
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

}
