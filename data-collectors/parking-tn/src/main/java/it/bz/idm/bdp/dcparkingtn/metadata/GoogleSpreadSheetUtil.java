package it.bz.idm.bdp.dcparkingtn.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DeleteRangeRequest;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

@Lazy
@Component
public class GoogleSpreadSheetUtil extends GoogleAuthenticator {

	@Value("${SPREADSHEET_ID}")
	private String spreadSheetId;

	@Value("${SPREADSHEET_RANGE}")
	private String spreadsheetRange;

	@Value("${SPREADSHEET_NAME}")
	private String spreadsheetName;

	private Sheets service;

	@Override
	public void initGoogleClient(NetHttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY, Credential credential)
			throws IOException {
		service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName("parking-offstreet-meranobolzano-dc").build();
	}

	public ValueRange getAllValues() {
		try {
			return service.spreadsheets().values()
					.get(spreadSheetId, spreadsheetName + "!" + spreadsheetRange).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getSheetId() {
		try {
			Spreadsheet spreadSheet = service.spreadsheets().get(spreadSheetId).execute();
			for (Sheet sheet : spreadSheet.getSheets()) {
				if (sheet.getProperties().getTitle().trim().equals(spreadsheetName))
					return sheet.getProperties().getSheetId();
			}
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void appendRows(List<List<Object>> values) throws IOException {
		if (values == null || values.isEmpty())
			return;

		ValueRange body = new ValueRange()
				.setValues(values);

		service.spreadsheets().values()
				.append(spreadSheetId, spreadsheetName + "!" + spreadsheetRange, body)
				.setValueInputOption("RAW")
				.setInsertDataOption("INSERT_ROWS")
				.execute();
	}

	public void deleteRows(int sheetId, List<Integer> deletedStationsRowIndexes) throws IOException {

		// delete those stations from the sheet
		for (int rowIndex : deletedStationsRowIndexes) {
			List<Request> requests = new ArrayList<>();

			DeleteRangeRequest deleteRequest = new DeleteRangeRequest()
					.setRange(new GridRange().setSheetId(sheetId)
							.setStartColumnIndex(0).setEndColumnIndex(26)
							.setStartRowIndex(rowIndex).setEndRowIndex(rowIndex + 1))
					.setShiftDimension("ROWS");

			requests.add(new Request()
					.setDeleteRange(deleteRequest));

			BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);

			service.spreadsheets().batchUpdate(spreadSheetId, body).execute();
		}
	}

}
