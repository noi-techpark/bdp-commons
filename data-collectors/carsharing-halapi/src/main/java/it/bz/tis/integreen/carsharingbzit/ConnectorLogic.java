/*
carsharing-ds: car sharing datasource for the integreen cloud

Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.bz.tis.integreen.carsharingbzit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.bz.idm.bdp.IntegreenPushable;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.carsharing.CarsharingStationDto;
import it.bz.idm.bdp.dto.carsharing.CarsharingVehicleDto;
import it.bz.idm.bdp.util.IntegreenException;
import it.bz.tis.integreen.carsharingbzit.api.ApiClient;
import it.bz.tis.integreen.carsharingbzit.api.BoundingBox;
import it.bz.tis.integreen.carsharingbzit.api.GetStationRequest;
import it.bz.tis.integreen.carsharingbzit.api.GetStationResponse;
import it.bz.tis.integreen.carsharingbzit.api.GetVehicleRequest;
import it.bz.tis.integreen.carsharingbzit.api.GetVehicleResponse;
import it.bz.tis.integreen.carsharingbzit.api.ListStationByBoundingBoxRequest;
import it.bz.tis.integreen.carsharingbzit.api.ListVehicleOccupancyByStationRequest;
import it.bz.tis.integreen.carsharingbzit.api.ListVehicleOccupancyByStationResponse;
import it.bz.tis.integreen.carsharingbzit.api.ListVehicleOccupancyByStationResponse.VehicleAndOccupancies;
import it.bz.tis.integreen.carsharingbzit.api.ListVehiclesByStationsRequest;
import it.bz.tis.integreen.carsharingbzit.api.ListVehiclesByStationsResponse;
import it.bz.tis.integreen.carsharingbzit.api.ListVehiclesByStationsResponse.StationAndVehicles;

/**
 *
 * @author Davide Montesin <d@vide.bz>
 */
public class ConnectorLogic
{
	final static long             INTERVALL                    = 10L * 60L * 1000L;

	public static final String    CARSHARINGSTATION_DATASOURCE = "CarsharingStation";
	public static final String    CARSHARINGCAR_DATASOURCE     = "CarsharingCar";

	static final SimpleDateFormat SIMPLE_DATE_FORMAT           = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"); // 2014-09-15T12:00:00

	static HashMap<String, String[]> process(ApiClient apiClient,
			String[] cityUIDs,
			IntegreenPushable xmlrpcPusher,
			HashMap<String, String[]> vehicleIdsByStationIds,
			long updateTime,
			ActivityLog activityLog,
			ArrayList<ActivityLog> lock) throws IOException
	{
		if (vehicleIdsByStationIds == null) // Do a full sync
		{
			vehicleIdsByStationIds = processSyncStations(apiClient, cityUIDs, xmlrpcPusher, activityLog, lock);
		}
		processPusDatas(apiClient, xmlrpcPusher, vehicleIdsByStationIds, updateTime, activityLog, lock);
		return vehicleIdsByStationIds;
	}

	static HashMap<String, String[]> processSyncStations(ApiClient apiClient,
			String[] cityUIDs,
			IntegreenPushable xmlrpcPusher,
			ActivityLog activityLog,
			ArrayList<ActivityLog> lock) throws IOException
	{
		///////////////////////////////////////////////////////////////
		// Stations by city
		///////////////////////////////////////////////////////////////

		//      ListStationsByCityRequest request = new ListStationsByCityRequest(cityUIDs);
		//      ListStationsByCityResponse response = apiClient.callWebService(request, ListStationsByCityResponse.class);
		//      CarsharingStationDto[] stations = response.getCityAndStations()[0].getStation();
		///////////////////////////////////////////////////////////////
		// Stations by Bounding Box
		///////////////////////////////////////////////////////////////

		Set<String> stationSet = fetchStations(apiClient);
		///////////////////////////////////////////////////////////////
		// Stations details
		///////////////////////////////////////////////////////////////



		String[] stationIds = stationSet.toArray(new String[stationSet.size()]);
		GetStationRequest requestGetStation = new GetStationRequest(stationIds);
		GetStationResponse responseGetStation = apiClient.callWebService(requestGetStation, GetStationResponse.class);

		///////////////////////////////////////////////////////////////
		// Vehicles by stations
		///////////////////////////////////////////////////////////////

		ListVehiclesByStationsRequest vehicles = new ListVehiclesByStationsRequest(stationIds);
		ListVehiclesByStationsResponse responseVehicles = apiClient.callWebService(vehicles,
				ListVehiclesByStationsResponse.class);

		///////////////////////////////////////////////////////////////
		// Vehicles details
		///////////////////////////////////////////////////////////////

		HashMap<String, String[]> vehicleIdsByStationIds = new HashMap<>();
		ArrayList<String> veichleIds = new ArrayList<String>();
		for (StationAndVehicles stationVehicles : responseVehicles.getStationAndVehicles())
		{
			String[] vehicleIds = new String[stationVehicles.getVehicle().length];
			vehicleIdsByStationIds.put(stationVehicles.getStation().getId(), vehicleIds);
			for (int i = 0; i < stationVehicles.getVehicle().length; i++)
			{
				it.bz.tis.integreen.carsharingbzit.api.CarsharingVehicleDto carsharingVehicleDto = stationVehicles.getVehicle()[i];
				veichleIds.add(carsharingVehicleDto.getId());
				vehicleIds[i] = carsharingVehicleDto.getId();
			}
		}

		GetVehicleRequest requestVehicleDetails = new GetVehicleRequest(veichleIds.toArray(new String[0]));
		GetVehicleResponse responseVehicleDetails = apiClient.callWebService(requestVehicleDetails,
				GetVehicleResponse.class);

		///////////////////////////////////////////////////////////////
		// Write data to integreen
		///////////////////////////////////////////////////////////////

		it.bz.tis.integreen.carsharingbzit.api.CarsharingStationDto[] stations = responseGetStation.getStation();
		StationList castedStations = castToBDP(stations);
		Object result = xmlrpcPusher.syncStations(CARSHARINGSTATION_DATASOURCE, castedStations);
		if (result instanceof IntegreenException)
		{
			throw new IOException("IntegreenException");
		}

		synchronized (lock)
		{
			activityLog.report += "syncStations("
					+ CARSHARINGSTATION_DATASOURCE
					+ "): "
					+ stations.length
					+ "\n";
		}
		it.bz.tis.integreen.carsharingbzit.api.CarsharingVehicleDto[] vehiclesDtos = responseVehicleDetails.getVehicle();
		StationList castedVehicles = castToBDP(vehiclesDtos);
		result = xmlrpcPusher.syncStations(CARSHARINGCAR_DATASOURCE, castedVehicles);
		if (result instanceof IntegreenException)
		{
			throw new IOException("IntegreenException");
		}

		synchronized (lock)
		{
			activityLog.report += "syncStations("
					+ CARSHARINGCAR_DATASOURCE
					+ "): "
					+ vehiclesDtos.length
					+ "\n";
		}
		return vehicleIdsByStationIds;
	}

	public static Set<String> fetchStations(ApiClient apiClient) throws IOException {
		List<BoundingBox> boxes = new ArrayList<BoundingBox>();
		boxes.add(new BoundingBox(10.375214,46.459147,11.059799,46.86113));
		boxes.add(new BoundingBox(11.015081,46.450277,11.555557,46.765265));
		boxes.add(new BoundingBox(11.458354,46.533418,11.99883,46.847924));
		boxes.add(new BoundingBox(11.166573,46.218327,11.521568,46.455303));
		boxes.add(new BoundingBox(11.092758,46.794448,11.959305,47.018653));
		boxes.add(new BoundingBox(11.959305,46.598506,12.423477,47.098175));

		Set<String> stationSet = new HashSet<String>();
		for (BoundingBox box:boxes){
			ListStationByBoundingBoxRequest request = new ListStationByBoundingBoxRequest(box);
			ListStationsByBoundingBoxResponse response = apiClient.callWebService(request, ListStationsByBoundingBoxResponse.class);
			if (response != null){
				it.bz.tis.integreen.carsharingbzit.api.CarsharingStationDto[] stations = response.getStation();
				for (int i = 0; i < stations.length; i++)
				{
					stationSet.add(stations[i].getId());
				}
			}
		}
		return stationSet;
	}

	private static StationList castToBDP(
			it.bz.tis.integreen.carsharingbzit.api.CarsharingVehicleDto[] vehicles) {
		StationList dtos = new StationList();
		for (it.bz.tis.integreen.carsharingbzit.api.CarsharingVehicleDto dto : vehicles){
			StationDto castedDto = new StationDto();
			castedDto.setId(dto.getId());
			castedDto.setLatitude(dto.getLatitude());
			castedDto.setLongitude(dto.getLongitude());
			castedDto.setName(dto.getName());
			castedDto.setOrigin(dto.getOrigin());
			castedDto.getMetaData().put("brand",dto.getBrand());
			castedDto.setCoordinateReferenceSystem(dto.getCrs());
			castedDto.getMetaData().put("licensePlate",dto.getLicensePlate());
			castedDto.getMetaData().put("model",dto.getModel());
			castedDto.getMetaData().put("showType",dto.getShowType());
			castedDto.setParentId(dto.getStationId());
			dtos.add(castedDto);
		}
		return dtos;
	}

	private static StationList castToBDP(
			it.bz.tis.integreen.carsharingbzit.api.CarsharingStationDto[] stations) {
		StationList dtos = new StationList();
		for (it.bz.tis.integreen.carsharingbzit.api.CarsharingStationDto dto : stations){
			StationDto castedDto = new StationDto();
			castedDto.getMetaData().put("access",dto.getAccess());
			castedDto.getMetaData().put("bookMode",dto.getBookMode());
			castedDto.getMetaData().put("company",dto.getCompany());
			castedDto.setCoordinateReferenceSystem(dto.getCrs());
			castedDto.getMetaData().put("hasFixedParking",dto.isHasFixedParking());
			castedDto.setId(dto.getId());
			castedDto.setLatitude(dto.getLatitude());
			castedDto.setLongitude(dto.getLongitude());
			castedDto.setName(dto.getName());
			castedDto.setOrigin(dto.getOrigin());
			dtos.add(castedDto);
		}
		return dtos;
	}

	static void processPusDatas(ApiClient apiClient,
			IntegreenPushable jsonPusher,
			HashMap<String, String[]> vehicleIdsByStationIds,
			long updateTime,
			ActivityLog activityLog,
			ArrayList<ActivityLog> lock) throws IOException
	{
		///////////////////////////////////////////////////////////////
		// Read vehicles occupancy and calculate summaries
		///////////////////////////////////////////////////////////////

		String created = String.valueOf(updateTime);

		// Current and forecast
		for (long forecast : new long[] { 0, 30L * 60L * 1000L })
		{
			String begin = String.valueOf(updateTime + forecast);
			// TODO begin buffer depends on car type
			String begin_carsharing = SIMPLE_DATE_FORMAT.format(new Date(updateTime - 30L * 60L * 1000L + forecast));
			String end = SIMPLE_DATE_FORMAT.format(new Date(updateTime + INTERVALL + forecast));

			String[] stationIds = vehicleIdsByStationIds.keySet().toArray(new String[0]);
			Arrays.sort(stationIds);
			DataMapDto<RecordDtoImpl> stationData = new DataMapDto<>();
			DataMapDto<RecordDtoImpl> vehicleData = new DataMapDto<>();
			for (String stationId : stationIds)
			{
				String[] vehicleIds = vehicleIdsByStationIds.get(stationId);
				ListVehicleOccupancyByStationRequest occupancyByStationRequest = new ListVehicleOccupancyByStationRequest(begin_carsharing,
						end,
						stationId,
						vehicleIds);

				ListVehicleOccupancyByStationResponse responseOccupancy = apiClient.callWebService(occupancyByStationRequest,
						ListVehicleOccupancyByStationResponse.class);

				VehicleAndOccupancies[] occupancies = responseOccupancy.getVehicleAndOccupancies();
				if (occupancies== null)
					continue;
				if (occupancies.length != vehicleIds.length) // Same number of responses as the number to requests
				{
					throw new IllegalStateException();
				}
				int free = 0;
				for (VehicleAndOccupancies vehicleOccupancy : occupancies)
				{
					if (vehicleOccupancy.getOccupancy().length > 1)
					{
						throw new IllegalStateException("Why???");
					}
					int state = 0; // free
					if (vehicleOccupancy.getOccupancy().length == 1)
					{
						state = 1;
					}
					else
					{
						free++;
					}
					DataMapDto<RecordDtoImpl> typeMap = new DataMapDto<>();
					vehicleData.getBranch().put(vehicleOccupancy.getVehicle().getId(), typeMap);
					String type = "unknown";
					if (begin.equals(created))
						type = DataTypeDto.AVAILABILITY;
					else
						type = DataTypeDto.FUTURE_AVAILABILITY;
					DataMapDto<RecordDtoImpl> dataMap = typeMap.getBranch().get(type);
					SimpleRecordDto simpleRecordDto = new SimpleRecordDto(updateTime + forecast,state+0.,600);
					if (dataMap == null){
						List<RecordDtoImpl> dtos = new ArrayList<>();
						dtos.add(simpleRecordDto);
						dataMap = new DataMapDto<>(dtos);
						typeMap.getBranch().put(type, new DataMapDto<>(dtos));
					}
					List<RecordDtoImpl> dataRecords = new ArrayList<>();
					dataRecords.add(simpleRecordDto);
					dataMap.setData(dataRecords);
				}
				List<RecordDtoImpl> dtos = new ArrayList<RecordDtoImpl>();
				DataMapDto<RecordDtoImpl> typeMap = new DataMapDto<>();
				typeMap.getBranch().put(DataTypeDto.NUMBER_AVAILABE, new DataMapDto<>(dtos));
				if (begin.equals(created))
					dtos.add(new SimpleRecordDto(updateTime + forecast, free+0.,600));
				stationData.getBranch().put(stationId, typeMap );

			}

			///////////////////////////////////////////////////////////////
			// Write data to integreen
			///////////////////////////////////////////////////////////////

			Object result = jsonPusher.pushData(CARSHARINGSTATION_DATASOURCE, stationData);
			if (result instanceof IntegreenException)
			{
				throw new IOException("IntegreenException");
			}
			synchronized (lock)
			{
				activityLog.report += "pushData(" + CARSHARINGSTATION_DATASOURCE + "): " + stationData.getBranch().size()+ "\n";
			}
			result = jsonPusher.pushData(CARSHARINGCAR_DATASOURCE, vehicleData);
			if (result instanceof IntegreenException)
			{
				throw new IOException("IntegreenException");
			}
			synchronized (lock)
			{
				activityLog.report += "pushData(" + CARSHARINGCAR_DATASOURCE + "): " + vehicleData.getBranch().size() + "\n";
			}
		}
	}
}
