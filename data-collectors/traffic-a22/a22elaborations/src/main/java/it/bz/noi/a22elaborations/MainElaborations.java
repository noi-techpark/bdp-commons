package it.bz.noi.a22elaborations;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@DisallowConcurrentExecution
public class MainElaborations implements Job
{

	static class WindowStepLength
	{
		int step;
		int length;
	}

	private static Logger log = Logger.getLogger(MainElaborations.class);

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{
		try
		{
			log.info("Start MainElaborations");

			A22TrafficJSONPusher pusher = new A22TrafficJSONPusher();

			// SyncDatatype.saveDatatypes();

			Connection connection = Utility.createConnection();

			StationList stations = SyncStation.saveStations(connection);

			WindowStepLength winStepLength = readElaborationProperties();

			long now = System.currentTimeMillis();

			for (StationDto station : stations)
			{
				String stationcode = station.getId();
				// System.out.println(stationcode);
				// System.out.println(station.getName());

				long lastTimestamp = ((java.util.Date) pusher.getDateOfLastRecord(stationcode, null, null)).getTime();
				if (lastTimestamp <= 0)
				{
					Calendar calendar = Calendar.getInstance();
					calendar.set(2019, Calendar.JUNE, 21, 0, 0, 0);
					calendar.set(Calendar.MILLISECOND, 0);

					lastTimestamp = calendar.getTimeInMillis();
				}
				else
				{
					// 2019-07-10 d@vide.bz: go back 1 hour from last time elaboration because data can't be in realtime
					lastTimestamp -= 3600L * 1000L;
				}

				// loop over the windows
				while (lastTimestamp + winStepLength.length < now) // loop until the window is entirely in the past
				{
					log.debug("elaborating station: " + station.getName() + " window: " + new java.sql.Timestamp(lastTimestamp).toString());
					ArrayList<Vehicle> vehicles = readWindow(lastTimestamp, lastTimestamp + winStepLength.length,
							stationcode, connection);

					saveMeasurementAndCalculation(station, vehicles, lastTimestamp, winStepLength.length);

					lastTimestamp = lastTimestamp + winStepLength.step;
				}
			}
			connection.close();
		}
		catch (IOException | SQLException exxx)
		{
			throw new JobExecutionException(exxx);
		}
	}

	private WindowStepLength readElaborationProperties() throws IOException
	{
		try (InputStream in = getClass().getResourceAsStream("elaborations.properties"))
		{
			Properties prop = new Properties();
			prop.load(in);
			WindowStepLength result = new WindowStepLength();
			result.length = Integer.parseInt(prop.getProperty("windowLength"));
			result.step = Integer.parseInt(prop.getProperty("step"));
			return result;
		}
	}

	/**
	 * saves all calculations in bdp-core
	 * 
	 * @param vehiclesForStation
	 */
	private void saveMeasurementAndCalculation(StationDto station, ArrayList<Vehicle> vehicles, long lastTimestamp,
			long windowLength)
	{
		A22TrafficJSONPusher pusher = new A22TrafficJSONPusher();

		DataMapDto<RecordDtoImpl> dataMapDto = new DataMapDto<>();

		Map<String, Integer> classCounts = createVehicleCounts(vehicles);

		double equivalentVehicles = 0;

		for (Map.Entry<String, Integer> classCount : classCounts.entrySet())
		{

			if (classCount.getKey().equals(SyncDatatype.NR_LIGHT_VEHICLES))
			{
				equivalentVehicles += classCount.getValue();
			} else if (classCount.getKey().equals(SyncDatatype.NR_HEAVY_VEHICLES))
			{
				equivalentVehicles += 2.5 * classCount.getValue();
			}

			Timestamp timestamp = new Timestamp(lastTimestamp /* + windowLength / 2 */);
			SimpleRecordDto record = new SimpleRecordDto(timestamp.getTime(), classCount.getValue(),
					(int) (windowLength / 1000), System.currentTimeMillis());

			dataMapDto.addRecord(station.getId(), classCount.getKey(), record);

		}

		Timestamp timestamp = new Timestamp(lastTimestamp /* + windowLength / 2 */);
		SimpleRecordDto record = new SimpleRecordDto(timestamp.getTime(), equivalentVehicles,
				(int) (windowLength / 1000), System.currentTimeMillis());
		dataMapDto.addRecord(station.getId(), SyncDatatype.NR_EQUIVALENT_VEHICLES, record);

		Map<String, Double> classAvgSpeeds = createClassAvgSpeeds(vehicles);

		for (Map.Entry<String, Double> classAvgSpeed : classAvgSpeeds.entrySet())
		{
			Timestamp timestampAvgSpeed = new Timestamp(lastTimestamp /* + windowLength / 2 */);
			SimpleRecordDto recordAvgSpeed = new SimpleRecordDto(timestampAvgSpeed.getTime(), classAvgSpeed.getValue(),
					(int) (windowLength / 1000), System.currentTimeMillis());
			dataMapDto.addRecord(station.getId(), classAvgSpeed.getKey(), recordAvgSpeed);
		}

		Map<String, Double> classVarSpeeds = createClassVarSpeeds(vehicles, classAvgSpeeds);

		for (Map.Entry<String, Double> classVarSpeed : classVarSpeeds.entrySet())
		{
			Timestamp timestampVarSpeed = new Timestamp(lastTimestamp /* + windowLength / 2 */);
			SimpleRecordDto recordVarSpeed = new SimpleRecordDto(timestampVarSpeed.getTime(), classVarSpeed.getValue(),
					(int) (windowLength / 1000), System.currentTimeMillis());
			dataMapDto.addRecord(station.getId(), classVarSpeed.getKey(), recordVarSpeed);
		}

		Map<String, Double> classAvgs = createClassAvgs(vehicles, equivalentVehicles, windowLength);

		for (Map.Entry<String, Double> classAvg : classAvgs.entrySet())
		{
			Timestamp timestampAvg = new Timestamp(lastTimestamp /* + windowLength / 2 */);
			SimpleRecordDto recordAvg = new SimpleRecordDto(timestampAvg.getTime(), classAvg.getValue(),
					(int) (windowLength / 1000), System.currentTimeMillis());
			dataMapDto.addRecord(station.getId(), classAvg.getKey(), recordAvg);

		}

		pusher.pushData(dataMapDto);

	}

	/**
	 * create averages for different vehicle classes
	 * 
	 * @param vehicles
	 * @param equivalentVehicles
	 * @return map of average for each vehicle class
	 */
	private Map<String, Double> createClassAvgs(ArrayList<Vehicle> vehicles, double equivalentVehicles,
			long windowLength)
	{
		Map<String, Double> classCounts = new HashMap<String, Double>();

		double gapSum = 0;
		double headwaySum = 0;
		double meanSpacing = 0;
		double speedSum = 0;

		for (Vehicle vehicle : vehicles)
		{
			if (vehicle.getNr_classes_count() != 1)
				throw new IllegalStateException("vehicle classification not unique");

			gapSum += vehicle.getDistance();
			headwaySum += vehicle.getHeadway();
			meanSpacing += vehicle.getHeadway() * vehicle.getSpeed() / 3.6;
			speedSum += vehicle.getSpeed();
		}
		double avgHeadway = 0;
		double averageDensity = 0;
		double averageFlow = 0;
		double averageGap = 0;
		double averageSpeed = 0;

		if (vehicles.size() > 0)
		{
			avgHeadway = headwaySum / vehicles.size();
			meanSpacing = meanSpacing / vehicles.size();
			// averageDensity = 1/(meanSpacing/1000);
			// averageFlow = (1/avgHeadway)*3600;

			// calculate avgFlow from vehicles in interval and convert sec to h
			averageFlow = equivalentVehicles * 3600 / windowLength;
			averageSpeed = speedSum / vehicles.size();
			averageDensity = averageFlow / averageSpeed;
			averageGap = gapSum / vehicles.size();
		}

		classCounts.put(SyncDatatype.AVERAGE_GAP, averageGap);
		classCounts.put(SyncDatatype.AVERAGE_HEADWAY, avgHeadway);
		classCounts.put(SyncDatatype.AVERAGE_DENSITY, averageDensity);
		classCounts.put(SyncDatatype.AVERAGE_FLOW, averageFlow);
		return classCounts;
	}

	/**
	 * creates speed variance of each vehicle class
	 * 
	 * @param vehicles
	 * @param classAvgSpeeds
	 * @return map of each vehicles class with the speed variance
	 */
	private static Map<String, Double> createClassVarSpeeds(ArrayList<Vehicle> vehicles,
			Map<String, Double> classAvgSpeeds)
	{
		Map<String, Double> classCounts = new HashMap<String, Double>();

		double speedSumLight = 0.0;
		double speedSumHeavy = 0.0;
		double speedSumBuses = 0.0;
		int countLight = 0;
		int countHeavy = 0;
		int countBuses = 0;
		for (Vehicle vehicle : vehicles)
		{
			// 2019-06-21 d@vide.bz: if the classification is not unique then skip this vehicle
			if (vehicle.getNr_classes_avg() != 1)
				continue;

			if (vehicle.isClasse_1_avg() || vehicle.isClasse_2_avg() || vehicle.isClasse_4_avg())
			{
				speedSumLight += Math
						.pow(vehicle.getSpeed() - classAvgSpeeds.get(SyncDatatype.AVERAGE_SPEED_LIGHT_VEHICLES), 2);
				countLight++;
			} else if (vehicle.isClasse_3_avg() || vehicle.isClasse_6_avg() || vehicle.isClasse_7_avg()
					|| vehicle.isClasse_8_avg())
			{
				speedSumHeavy += Math
						.pow(vehicle.getSpeed() - classAvgSpeeds.get(SyncDatatype.AVERAGE_SPEED_HEAVY_VEHICLES), 2);
				countHeavy++;
			} else if (vehicle.isClasse_5_avg() || vehicle.isClasse_9_avg())
			{
				speedSumBuses += Math.pow(vehicle.getSpeed() - classAvgSpeeds.get(SyncDatatype.AVERAGE_SPEED_BUSES), 2);
				countBuses++;
			}
		}

		double speedVarLight = 0;
		double speedVarHeavy = 0;
		double speedVarBuses = 0;

		if (countLight > 0)
		{
			speedVarLight = speedSumLight / countLight;
		}
		if (countHeavy > 0)
		{
			speedVarHeavy = speedSumHeavy / countHeavy;
		}
		if (countBuses > 0)
		{
			speedVarBuses = speedSumBuses / countBuses;
		}

		classCounts.put(SyncDatatype.VARIANCE_SPEED_LIGHT_VEHICLES, speedVarLight);
		classCounts.put(SyncDatatype.VARIANCE_SPEED_HEAVY_VEHICLES, speedVarHeavy);
		classCounts.put(SyncDatatype.VARIANCE_SPEED_BUSES, speedVarBuses);

		return classCounts;
	}

	/**
	 * create for every vehicle class average speed
	 * 
	 * @param vehicles
	 * @return map of vehicle class with average speed
	 */
	private static Map<String, Double> createClassAvgSpeeds(ArrayList<Vehicle> vehicles)
	{
		Map<String, Double> classCounts = new HashMap<String, Double>();

		double speedSumLight = 0.0;
		double speedSumHeavy = 0.0;
		double speedSumBuses = 0.0;
		int countLight = 0;
		int countHeavy = 0;
		int countBuses = 0;
		for (Vehicle vehicle : vehicles)
		{
			// 2019-06-21 d@vide.bz: if the classification is not unique then skip this vehicle
			if (vehicle.getNr_classes_avg() != 1)
				continue;

			if (vehicle.isClasse_1_avg() || vehicle.isClasse_2_avg() || vehicle.isClasse_4_avg())
			{
				speedSumLight += vehicle.getSpeed();
				countLight++;
			} else if (vehicle.isClasse_3_avg() || vehicle.isClasse_6_avg() || vehicle.isClasse_7_avg()
					|| vehicle.isClasse_8_avg())
			{
				speedSumHeavy += vehicle.getSpeed();
				countHeavy++;
			} else if (vehicle.isClasse_5_avg() || vehicle.isClasse_9_avg())
			{
				speedSumBuses += vehicle.getSpeed();
				countBuses++;
			}
		}

		double speedAvgLight = 0;
		double speedAvgHeavy = 0;
		double speedAvgBuses = 0;

		if (countLight > 0)
		{
			speedAvgLight = speedSumLight / countLight;
		}
		if (countHeavy > 0)
		{
			speedAvgHeavy = speedSumHeavy / countHeavy;
		}
		if (countBuses > 0)
		{
			speedAvgBuses = speedSumBuses / countBuses;
		}

		classCounts.put(SyncDatatype.AVERAGE_SPEED_LIGHT_VEHICLES, speedAvgLight);
		classCounts.put(SyncDatatype.AVERAGE_SPEED_HEAVY_VEHICLES, speedAvgHeavy);
		classCounts.put(SyncDatatype.AVERAGE_SPEED_BUSES, speedAvgBuses);

		return classCounts;
	}

	/**
	 * read all vehicles in interval
	 * 
	 * @param from_ts
	 * @param to_ts
	 * @return list of vehicle objects
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public ArrayList<Vehicle> readWindow(long from_ts, long to_ts, String stationcode, Connection connection)
			throws SQLException, IOException
	{
		ArrayList<Vehicle> vehicles = new ArrayList<>();

		String query = Utility.readResourceText(MainElaborations.class, "read-window.sql");

		String anomalies = Utility.readResourceText(MainElaborations.class, "save-anomalies.sql");

		PreparedStatement ps_anomalies = connection.prepareStatement(anomalies);

		PreparedStatement ps = connection.prepareStatement(query);
		ps.setLong(1, from_ts / 1000);
		ps.setLong(2, to_ts / 1000);
		ps.setString(3, stationcode);
		ResultSet resultSet = ps.executeQuery();
		while (resultSet.next())
		{
			Vehicle vehicle = new Vehicle(resultSet.getString("stationCode"), resultSet.getLong("timestamp"),
					resultSet.getDouble("distance"), resultSet.getDouble("headway"), resultSet.getDouble("length"),
					resultSet.getInt("axles"), resultSet.getBoolean("against_traffic"), resultSet.getInt("class"),
					resultSet.getDouble("speed"), resultSet.getInt("direction"), resultSet.getDouble("vmed_50"),
					resultSet.getBoolean("classe_1_avg"), resultSet.getBoolean("classe_1_count"),
					resultSet.getBoolean("classe_2_avg"), resultSet.getBoolean("classe_2_count"),
					resultSet.getBoolean("classe_3_avg"), resultSet.getBoolean("classe_3_count"),
					resultSet.getBoolean("classe_4_avg"), resultSet.getBoolean("classe_4_count"),
					resultSet.getBoolean("classe_5_avg"), resultSet.getBoolean("classe_5_count"),
					resultSet.getBoolean("classe_6_avg"), resultSet.getBoolean("classe_6_count"),
					resultSet.getBoolean("classe_7_avg"), resultSet.getBoolean("classe_7_count"),
					resultSet.getBoolean("classe_8_avg"), resultSet.getBoolean("classe_8_count"),
					resultSet.getBoolean("classe_9_avg"), resultSet.getBoolean("classe_9_count"),
					resultSet.getInt("nr_classes_count"), resultSet.getInt("nr_classes_avg"));

			if (vehicle.getNr_classes_count() != 1 || vehicle.getNr_classes_avg() != 1)
			{
				// save into anomalies
				ps_anomalies.setString(1, vehicle.getStationcode());
				ps_anomalies.setLong(2, vehicle.getTimestamp());
				ps_anomalies.setDouble(3, vehicle.getDistance());
				ps_anomalies.setDouble(4, vehicle.getHeadway());
				ps_anomalies.setDouble(5, vehicle.getLength());
				ps_anomalies.setInt(6, vehicle.getAxles());
				ps_anomalies.setBoolean(7, vehicle.isAgainst_traffic());
				ps_anomalies.setInt(8, vehicle.getClass_nr());
				ps_anomalies.setDouble(9, vehicle.getSpeed());
				ps_anomalies.setInt(10, vehicle.getDirection());

				ps_anomalies.executeUpdate();
			}

			if (vehicle.getNr_classes_avg() == 1 && vehicle.getNr_classes_count() != 1)
				throw new IllegalStateException("Illegal condition?");

			// if this vehicle can at least be used for count then add it for processing!
			if (vehicle.getNr_classes_count() == 1)
			{
				vehicles.add(vehicle);
			}
		}

		return vehicles;
	}

	/**
	 * create vehicle counts for every vehicle class
	 * 
	 * @param vehicles
	 * @return map of vehicles classes and count
	 */
	public static Map<String, Integer> createVehicleCounts(ArrayList<Vehicle> vehicles)
	{
		Map<String, Integer> classCounts = new HashMap<String, Integer>();
		classCounts.put(SyncDatatype.NR_LIGHT_VEHICLES, 0);
		classCounts.put(SyncDatatype.NR_HEAVY_VEHICLES, 0);
		classCounts.put(SyncDatatype.NR_BUSES, 0);
		for (Vehicle vehicle : vehicles)
		{
			if (vehicle.getNr_classes_count() != 1)
				throw new IllegalStateException("Illegal condition"); // 2019-06-21 d@vide.bz: or continue

			if (vehicle.isClasse_1_count() || vehicle.isClasse_2_count() || vehicle.isClasse_4_count())
			{
				classCounts.put(SyncDatatype.NR_LIGHT_VEHICLES, classCounts.get(SyncDatatype.NR_LIGHT_VEHICLES) + 1);
			} else if (vehicle.isClasse_3_count() || vehicle.isClasse_6_count() || vehicle.isClasse_7_count()
					|| vehicle.isClasse_8_count())
			{
				classCounts.put(SyncDatatype.NR_HEAVY_VEHICLES, classCounts.get(SyncDatatype.NR_HEAVY_VEHICLES) + 1);
			} else if (vehicle.isClasse_5_count() || vehicle.isClasse_9_count())
			{
				classCounts.put(SyncDatatype.NR_BUSES, classCounts.get(SyncDatatype.NR_BUSES) + 1);
			}
		}

		return classCounts;
	}

	/*
	 * Method used only for development/debugging
	 */
	public static void main(String[] args) throws JobExecutionException
	{
		new MainElaborations().execute(null);
	}

}
