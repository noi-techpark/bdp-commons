package it.bz.idm.bdp.airquality.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import it.bz.idm.bdp.airquality.dto.AQBlockDto;
import it.bz.idm.bdp.airquality.dto.AQStationDto;
import it.bz.idm.bdp.airquality.parser.AirQualityParser.BlockContext;
import it.bz.idm.bdp.airquality.parser.AirQualityParser.DatasetContext;
import it.bz.idm.bdp.airquality.parser.AirQualityParser.DateContext;
import it.bz.idm.bdp.airquality.parser.AirQualityParser.RowContext;
import it.bz.idm.bdp.airquality.parser.AirQualityParser.TimeContext;

public class MyAirQualityListener extends AirQualityBaseListener {

	private static final Logger log = LoggerFactory.getLogger(MyAirQualityListener.class.getName());

	private List<Integer> validStations;
	private List<Integer> validParameters;
	private List<Integer> validErrors;
	private List<Character> validMetrics;
	private List<AQStationDto> stations;
	private AQStationDto curStation;
	private AQBlockDto curBlock;
	private long stationsCount = 0;
	private long stationsSkipped = 0;
	private long blocksCount = 0;
	private long blocksSkipped = 0;
	private long keyvalCount = 0;
	private long keyvalSkipped = 0;

	public MyAirQualityListener(List<Integer> validStations, List<Integer> validParameters,
			List<Character> validMetrics, List<Integer> validErrors) {
		this.validStations = validStations;
		this.validParameters = validParameters;
		this.validMetrics = validMetrics;
		this.validErrors = validErrors;
	}

	public Map<String, Long> getStatistics() {
		Map<String, Long> stats = new HashMap<String, Long>();
		stats.put("stations total", stationsCount);
		stats.put("stations skipped", stationsSkipped);
		stats.put("blocks inside valid stations total", blocksCount);
		stats.put("blocks inside valid stations skipped", blocksSkipped);
		stats.put("key-value inside valid blocks total", keyvalCount);
		stats.put("key-value inside valid blocks skipped", keyvalSkipped);
		return stats;
	}

	public List<AQStationDto> getStations() {
		return stations;
	}

	@Override
	public void enterDataset(DatasetContext ctx) {
		stations = new ArrayList<AQStationDto>();
	}

	@Override
	public void exitRow(RowContext ctx) {
		if (curStation == null)
			return;
		stations.add(curStation);
	}

	@Override
	public void enterStation(AirQualityParser.StationContext ctx) {
		try {
			int stationID = Integer.valueOf(ctx.decimal().getText());

			stationsCount++;
			if (validStations.contains(stationID)) {
				curStation = new AQStationDto();
				curStation.setStation(stationID);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			curStation = null;
			stationsSkipped++;
		}
	}

	@Override
	public void enterDate(DateContext ctx) {
		if (curStation == null)
			return;
		try {
			curStation.setDay(Integer.valueOf(ctx.getChild(0).getText()));
			curStation.setMonth(Integer.valueOf(ctx.getChild(2).getText()));
			curStation.setYear(Integer.valueOf(ctx.getChild(4).getText()));
		} catch (Exception e) {
			curStation = null;
			stationsSkipped++;
			return;
		}
	}

	@Override
	public void enterTime(TimeContext ctx) {
		if (curStation == null)
			return;
		try {
			curStation.setHour(Integer.valueOf(ctx.getChild(0).getText()));
			curStation.setMinute(Integer.valueOf(ctx.getChild(2).getText()));
			curStation.setSecond(Integer.valueOf(ctx.getChild(4).getText()));
		} catch (Exception e) {
			curStation = null;
			stationsSkipped++;
			return;
		}
	}

	@Override
	public void enterBlock(BlockContext ctx) {
		if (curStation == null)
			return;

		try {
			int blockType = Integer.valueOf(ctx.getChild(0).getText());

			blocksCount++;
			if (validParameters.contains(blockType)) {
				curBlock = new AQBlockDto(blockType);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			curBlock = null;
			blocksSkipped++;
		}
	}

	@Override
	public void enterKeyval(AirQualityParser.KeyvalContext ctx) {
		/*
		 * A valid key-value pair consists of a key contained in the valid keys set, and
		 * a value that is either a number, an asterisk if the measurement is missing,
		 * or an empty string if an aggregation could not be calculated. In addition, we
		 * must check for additional wrong key representations due to incomplete or
		 * wrong key-value pairs on the input-side. Error codes must be found inside the
		 * valid-error-codes set.
		 *
		 * As defined in https://github.com/idm-suedtirol/bdp-environment/issues/1#issuecomment-364018016,
		 * we accept asterisks and set the value to 0. In addition, if some keys contain only empty strings,
		 * we set them to NULL to see that measurement at this time exist, but it is not possible to calculate
		 * an aggregated value.
		 */
		if (curStation == null)
			return;

		if (curBlock == null)
			return;

		try {
			Character keyToken = ctx.getChild(0).getText().trim().charAt(0);
			String valueToken = ctx.number().getText().trim();
			Double value = null;

			keyvalCount++;

			if (valueToken.equals("*")) {
				value = 0.0;
			} else if (valueToken.equals("")) {
				value = null;
			} else {
				try {
					value = Double.parseDouble(valueToken);
				} catch (NumberFormatException e) {
					keyvalSkipped++;
					// log.debug("SKIP: key = {}; value = {}", keyToken, valueToken);
					return;
				}
			}

			/* Errors have key = R */
			if (keyToken.equals('R')) {
				if (validErrors.contains(value.intValue())) {
					curBlock.addKeyValue(keyToken, value);
				} else {
					keyvalSkipped++;
					// log.debug("SKIP: key = {}; value = {} (invalid error code)", keyToken, value);
					return;
				}
			}

			/*
			 * There is no need to check the length of key-strings, because the parser
			 * already did that.
			 */
			if (validMetrics.contains(keyToken)) {
				curBlock.addKeyValue(keyToken, value);
			} else {
				keyvalSkipped++;
				// log.debug("SKIP: key = {}; value = {} (invalid measurement type)", keyToken, value);
				return;
			}

		} catch (Exception e) {
			log.error("Unknown error: {}", e.getMessage());
			log.error(ExceptionUtils.getStackTrace(e));
		}
	}

	@Override
	public void exitBlock(BlockContext ctx) {
		if (curStation == null)
			return;
		if (curBlock == null)
			return;
		curStation.addBlock(curBlock);
	}

}
