package it.bz.odh.spreadsheets.services;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.sheets.v4.Sheets;
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

@Lazy
@Component
public class GoogleSpreadSheetDataFetcher extends GoogleAuthenticator{

	@Value("${spreadsheetId}")
	private String spreadhSheetId;

    Sheets service;

    @Value("${spreadsheet.range}")
	private String spreadsheetRange;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

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
		Request noteClearRequest = new Request().setRepeatCell(new RepeatCellRequest()
				.setCell(new CellData().setNote(null))
				.setRange(new GridRange().setSheetId(sheetId)
						.setStartRowIndex(1)
						.setStartColumnIndex(collumnPosition))
				.setFields("note"));
		list.add(clearRequest);
		list.add(noteClearRequest);
		for (Integer missing : missingPositions) {
			Request colorRequest = new Request().setRepeatCell(new RepeatCellRequest()
					.setCell(new CellData().setUserEnteredFormat(new CellFormat().setBackgroundColor(backgroundColor)))
					.setRange(new GridRange().setSheetId(sheetId)
							.setStartRowIndex(missing).setEndRowIndex(missing+1)
							.setStartColumnIndex(collumnPosition).setEndColumnIndex(collumnPosition+1))
					.setFields("userEnteredFormat.backgroundColor"));
			Request noteRequest = new Request().setRepeatCell(new RepeatCellRequest()
					.setCell(new CellData().setNote("Last validity check: "+ formatter.format(LocalTime.now(ZoneId.of("Europe/Rome")))))
					.setRange(new GridRange().setSheetId(sheetId)
							.setStartRowIndex(missing).setEndRowIndex(missing+1)
							.setStartColumnIndex(collumnPosition).setEndColumnIndex(collumnPosition+1))
					.setFields("note"));
			list.add(colorRequest);
			list.add(noteRequest);
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
	@Override
	public void initGoogleClient(NetHttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY, Credential credential)
			throws IOException {
        service = new Sheets.Builder(HTTP_TRANSPORT,JSON_FACTORY,credential).build();
	}

}
