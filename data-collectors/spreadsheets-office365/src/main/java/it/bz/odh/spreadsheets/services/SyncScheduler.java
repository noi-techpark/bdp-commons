// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.spreadsheets.services;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.odh.spreadsheets.dto.DataTypeWrapperDto;
import it.bz.odh.spreadsheets.dto.MappingResult;
import it.bz.odh.spreadsheets.utils.DataMappingUtil;
import it.bz.odh.spreadsheets.utils.S3FileUtil;
import it.bz.odh.spreadsheets.utils.SharepointFileUtil;
import it.bz.odh.spreadsheets.utils.WorkbookUtil;

// The scheduler could theoretically be replaced by Microsoft Graphs Change Notifications
// So you don't need to poll last date changed with a cron job, but get notified, when changes are made
//
// Change notifications docs:
// https://docs.microsoft.com/en-us/graph/api/resources/webhooks?view=graph-rest-1.0
//
// StackExchange discussion about change notifications with Sharepoint
// https://sharepoint.stackexchange.com/questions/264609/does-the-microsoft-graph-support-driveitem-change-notifications-for-sharepoint-o

@Service
public class SyncScheduler {

	private static final Logger logger = LoggerFactory.getLogger(SyncScheduler.class);

	private Function<DataTypeWrapperDto, DataTypeDto> mapper = (dto) -> {
		return dto.getType();
	};

	@Lazy
	@Autowired
	private ODHClient odhClient;

	@Autowired
	private WorkbookUtil workbookUtil;

	@Autowired
	private DataMappingUtil mappingUtil;

	@Autowired
	private SharepointFileUtil sharepointFileUtil;

	@Autowired
	private S3FileUtil s3FileUtil;

	@Value("${sharepoint.fetch-files}")
	private boolean fetchFiles;

	@Value("${uselocalfile}")
	private boolean useLocalFile;

	@Value("${aws.bucket-url}")
	private String bucketUrl;

	@PostConstruct
    private void postConstruct() {
		if(useLocalFile) {
			Workbook localWorkbook = workbookUtil.getLocalWorkbook("NOI-Techpark-MapsBackend.xlsx");
			try {
				syncDataWithBdp(localWorkbook);
			} catch (Exception e) {
				logger.error("Error while syncing local file with BDP");
			}
		}
	}

	/**
	 * Cron job to check changes of the Spreadsheet in Sharepoint
	 * If changes where made, data gets uploaded to the BDP
	 *
	 * @throws Exception
	 */
	@Scheduled(cron = "${cron}")
	public void checkSharepoint() throws Exception {
		logger.info("Cron job manual sync started");
		Workbook sheet = workbookUtil.checkWorkbook();
		if (sheet != null) {
			logger.info("Syncing data with BDP");
			syncDataWithBdp(sheet);
			logger.info("Done: Syncing data with BDP");
		} else
			logger.debug("No new changes detected, skip sync with BDP");

		logger.info("Cron job manual sync end");
	}

	/**
	 * Converts a XSSFWorkbook to BDP Stations
	 *
	 * @throws Exception
	 */
	private void syncDataWithBdp(Workbook workbook) throws Exception {
		logger.info("Start data synchronization");

		Iterator<Sheet> sheetIterator = workbook.sheetIterator();
		StationList dtos = new StationList();
		List<DataTypeWrapperDto> types = new ArrayList<DataTypeWrapperDto>();

		logger.debug("Start reading spreadsheet");

		int index = 0;
		while (sheetIterator.hasNext()) {
			Sheet sheet = sheetIterator.next();

			// convert values of sheet to List<List<Object>> to be able to map data with
			// data-mapping of dc-googlespreadsheets
			List<List<Object>> values = new ArrayList<>();
			Iterator<Row> rowIterator = sheet.rowIterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				row.getLastCellNum();
				List<Object> rowList = new ArrayList<>();

				for (int i = 0; i < row.getLastCellNum(); i++) {
					Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
					// get value, if formula is defined
					String value = getCellValue(cell);
					rowList.add(value);
				}
				values.add(rowList);
			}

			try {
				if (values.isEmpty() || values.get(0) == null)
					throw new IllegalStateException(
							"Spreadsheet " + sheet.getSheetName() + " has no header row. Needs to start on top left.");
				MappingResult result = mappingUtil.mapSheet(values, sheet.getSheetName(), index); // TODO ask what id
																									// should be put
																									// here
				index++;
				if (!result.getStationDtos().isEmpty())
					dtos.addAll(result.getStationDtos());
				if (result.getDataType() != null) {
					types.add(result.getDataType());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
		}

		// sync files from sharepoint with S3 bucket
		if (fetchFiles) {
			syncSharepointFilesWithS3(dtos);
			createFileMetadata(dtos);
		}

		// sync with ODH
		if (!dtos.isEmpty()) {
			logger.info("Synchronize stations if some where fetched and successfully parsed");
			odhClient.syncStations(dtos);
			logger.info("Synchronize stations completed");
		}
		if (!types.isEmpty()) {
			logger.info("Synchronize data types/type-metadata if some where fetched and successfully parsed");
			List<DataTypeDto> dTypes = types.stream().map(mapper).collect(Collectors.toList());
			odhClient.syncDataTypes(dTypes);
			logger.info("Synchronize datatypes completed");
		}
		if (!dtos.isEmpty() && !types.isEmpty()) {
			DataMapDto<? extends RecordDtoImpl> dto = new DataMapDto<RecordDtoImpl>();
			logger.info("Connect datatypes with stations through record");
			for (DataTypeWrapperDto typeDto : types) {
				SimpleRecordDto simpleRecordDto = new SimpleRecordDto(new Date().getTime(),
						typeDto.getSheetName(), 0);
				logger.trace("Connect {} with {}", dtos.get(0).getId(), typeDto.getType().getName());
				dto.addRecord(dtos.get(0).getId(), typeDto.getType().getName(),
						simpleRecordDto);
			}
			odhClient.pushData(dto);
		}

		logger.info("Data synchronization completed");
	}

	/**
	 * Gets always the cell value if a formula is defined
	 *
	 * @param cell the cell
	 * @return The String value of the formula, if a formula is defined
	 */
	private String getCellValue(Cell cell) {
		if (cell.getCellType() == CellType.FORMULA) {
			switch (cell.getCachedFormulaResultType()) {
				case BOOLEAN:
					return String.valueOf(cell.getBooleanCellValue());
				case NUMERIC:
					return String.valueOf(cell.getNumericCellValue());
				case STRING:
					return cell.getRichStringCellValue() != null ? cell.getRichStringCellValue().getString() : null;
				default:
					return cell.getStringCellValue();
			}
		} else
			return cell.toString();
	}

	/**
	 * Synchronizes the files from a sharepoint folder with an S3 bucket
	 * First the object listing from S3 gets fetched and then only files that are
	 * new or have a more recent lastModifiedDate get uploaded
	 *
	 * @param dtos
	 * @throws Exception
	 */
	private void syncSharepointFilesWithS3(StationList dtos) {
		logger.info("Fetch images from Sharepoint and upload to S3");

		Map<String, Date> objectListing = s3FileUtil.getObjectListing();

		for (StationDto dto : dtos) {
			Map<String, Object> metaData = dto.getMetaData();

			for (String key : metaData.keySet()) {
				if (key.contains("file")) {
					String imageName = metaData.get(key).toString();

					Date lastModifiedOnSharepoint = null;
					try {
						lastModifiedOnSharepoint = sharepointFileUtil.getLastTimeModified(imageName);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					if (!objectListing.containsKey(imageName)
							|| lastModifiedOnSharepoint == null
							|| objectListing.get(imageName).before(lastModifiedOnSharepoint)) {

						logger.debug("Fetching image {} from Sharepoint", imageName);
						HttpURLConnection conn;
						try {
							conn = sharepointFileUtil.fetchFile(imageName);
							logger.debug("Fetching image {}} from Sharepoint done", imageName);

							logger.debug("Upload image {} to S3", imageName);
							s3FileUtil.uploadFile(conn.getInputStream(), imageName, conn.getContentLength());
							logger.debug("Upload image {} to S3 done", imageName);
						} catch (Exception e) {
							logger.debug("Fetching image {} from Sharepoint FAILED", imageName);
							e.printStackTrace();
						}
					}
				}
			}
		}
		logger.info("Fetch images from Sharepoint and upload to S3 done");
	}

	private void createFileMetadata(StationList dtos) {
		for (StationDto dto : dtos) {
			Map<String, Object> metaData = dto.getMetaData();

			for (String key : metaData.keySet()) {
				if (key.contains("file")) {
					String imageName = metaData.get(key).toString();

					// add S3 bucket URL to image metadata
					Map<String, String> imageMetadata = new HashMap<>();
					imageMetadata.put("link", bucketUrl + imageName);
					imageMetadata.put("license", "");
					imageMetadata.put("name", imageName);

					metaData.replace(key, imageMetadata);

				}
			}
		}
	}

}
