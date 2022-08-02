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
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
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
	private String spreadhSheetId;

	@Value("${SPREADSHEET_RANGE}")
	private String spreadsheetRange;

	@Value("${SPREADSHEET_NAME}")
	private String spreadsheetName;

	private Sheets service;

	@Override
	public void initGoogleClient(NetHttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY, Credential credential)
			throws IOException {
		service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName("parking-tn-dc").build();
	}

	public ValueRange getValues() {
		try {
			ValueRange sheet = service.spreadsheets().values()
					.get(spreadhSheetId, spreadsheetName + "!" + spreadsheetRange).execute();
			return sheet;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getSheetId() {
		try {
			Spreadsheet spreadSheet = service.spreadsheets().get(spreadhSheetId).execute();
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

	public boolean appendRows(List<List<Object>> values) throws IOException {
		if (values == null || values.size() == 0)
			return false;

		ValueRange body = new ValueRange()
				.setValues(values);

		AppendValuesResponse result = service.spreadsheets().values()
				.append(spreadhSheetId, spreadsheetName + "!" + spreadsheetRange, body)
				.setValueInputOption("RAW")
				.setInsertDataOption("INSERT_ROWS")
				.execute();

		// check if result was successful
		return result.getUpdates().getUpdatedRows() == values.size();
	}

	public boolean deleteRows(List<Integer> rowIndexes, int sheetId) throws IOException {

		for (int rowIndex : rowIndexes) {
			List<Request> requests = new ArrayList<>();

			DeleteRangeRequest deleteRequest = new DeleteRangeRequest()
					.setRange(new GridRange().setSheetId(sheetId)
							.setStartColumnIndex(0).setEndColumnIndex(26)
							.setStartRowIndex(rowIndex).setEndRowIndex(rowIndex + 1))
					.setShiftDimension("ROWS");

			requests.add(new Request()
					.setDeleteRange(deleteRequest));

			BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);

			service.spreadsheets().batchUpdate(spreadhSheetId, body).execute();
		}

		return true;
	}

}
