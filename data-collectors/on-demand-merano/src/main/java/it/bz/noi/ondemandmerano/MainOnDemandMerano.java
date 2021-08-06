package it.bz.noi.ondemandmerano;

import com.google.gson.Gson;
import it.bz.idm.bdp.dto.*;
import it.bz.noi.ondemandmerano.configuration.DatatypeConfiguration;
import it.bz.noi.ondemandmerano.configuration.DatatypesConfiguration;
import it.bz.noi.ondemandmerano.configuration.OnDemandMeranoConfiguration;
import it.bz.noi.ondemandmerano.model.OnDemandMeranoStop;
import it.bz.noi.ondemandmerano.model.OnDemandMeranoVehicle;
import it.bz.noi.ondemandmerano.pusher.StopJSONPusher;
import it.bz.noi.ondemandmerano.pusher.VehicleJSONPusher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MainOnDemandMerano {

    private static final Logger LOG = LogManager.getLogger(MainOnDemandMerano.class);

    @Autowired
    private OnDemandMeranoConnector onDemandMeranoConnector;
    @Autowired
    private OnDemandMeranoConfiguration onDemandMeranoConfiguration;
    @Autowired
    private DatatypesConfiguration datatypesConfiguration;

    @Autowired
    private StopJSONPusher stopJSONPusher;

    @Autowired
    private VehicleJSONPusher vehicleJSONPusher;

    public void executeStops() {
        LOG.info("MainOnDemandService executeStops");

        try {
            List<OnDemandMeranoStop> stops = onDemandMeranoConnector.getMyStops();
            LOG.debug("got {} stops", stops.size());
            StationList stopsStationList = new StationList();
            for(OnDemandMeranoStop stop: stops) {
                StationDto stationDto = new StationDto(onDemandMeranoConfiguration.getOrigin() + ":"+ stop.getId(),
                        stop.getTitle(),
                        stop.getPosition().getLatitude(),
                        stop.getPosition().getLongitude());
                stationDto.setOrigin(onDemandMeranoConfiguration.getOrigin());
                stationDto.setStationType(onDemandMeranoConfiguration.getStopsStationtype());
                stationDto.getMetaData().put("address", stop.getAddress());
                stationDto.getMetaData().put("groups", stop.getGroups());
                stationDto.getMetaData().put("region", stop.getRegion());
                stationDto.getMetaData().put("reference", stop.getReference());
                stationDto.getMetaData().put("type", stop.getType());
                stopsStationList.add(stationDto);
            }
            stopJSONPusher.syncStations(stopsStationList);
        } catch (Exception e) {
            LOG.error("reading stops failed ", e);
        }
    }

    public void executeVehicle() {
        LOG.info("MainOnDemandService executeVehicle");

        try {
            setupDataType();

            List<OnDemandMeranoVehicle> vehicles = onDemandMeranoConnector.getMyVehicles();
            LOG.debug("got {} vehicles", vehicles.size());
            List<StationDto> preStations = vehicleJSONPusher.fetchStations(onDemandMeranoConfiguration.getVehiclesStationtype(),
                    onDemandMeranoConfiguration.getOrigin());
            StationList stopsStationList = new StationList();
            DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();
            int positionMeasurementCount = 0;
            for(OnDemandMeranoVehicle vehicle: vehicles) {
                StationDto stationDto = new StationDto(vehicle.getLicensePlateNumber(),
                        vehicle.getLicensePlateNumber(),
                        vehicle.getPosition().getLatitude(),
                        vehicle.getPosition().getLongitude());
                stationDto.setOrigin(onDemandMeranoConfiguration.getOrigin());
                stationDto.setStationType(onDemandMeranoConfiguration.getVehiclesStationtype());
                stationDto.getMetaData().put("type", vehicle.getType());
                stationDto.getMetaData().put("operator", vehicle.getOperator());
                stationDto.getMetaData().put("capacityMax", vehicle.getCapacityMax());
                stationDto.getMetaData().put("capacityUsed", vehicle.getCapacityUsed());
                stopsStationList.add(stationDto);

                StationDto preStation = preStations.stream().filter(s -> s.getId().equals(stationDto.getId())).findFirst().orElseGet(() -> null);
                boolean updatePosition = true;
                if(preStation != null) {
                    double distance = calcDistanceBetweenStations(stationDto, preStation);
                    if(distance < 10) {
                        updatePosition = false;
                        // reset stations position
                        stationDto.setLatitude(preStation.getLatitude());
                        stationDto.setLongitude(preStation.getLongitude());
                    }
                }

                if(updatePosition) {
                    String recordTime = vehicle.getRecordTime();
                    ZonedDateTime recordTimeZonedDateTime = ZonedDateTime.parse(recordTime);
                    long recordTimeLong = recordTimeZonedDateTime.toInstant().toEpochMilli();

                    dataMap.addRecord(vehicle.getLicensePlateNumber(), datatypesConfiguration.getPosition().getKey(),
                            new SimpleRecordDto(recordTimeLong, new Gson().toJson(vehicle.getPosition()), onDemandMeranoConfiguration.getVehiclesPeriod()));
                    positionMeasurementCount++;
                }
            }
            vehicleJSONPusher.syncStations(stopsStationList);
            if(positionMeasurementCount > 0) {
                vehicleJSONPusher.pushData(dataMap);
            }
        } catch (Exception e) {
            LOG.error("reading vehicles failed", e);
        }

    }



    private void setupDataType() {
        List<DataTypeDto> dataTypeDtoList = new ArrayList<>();
        for (DatatypeConfiguration datatypeConfiguration : this.datatypesConfiguration.getAllDataTypes()) {
            dataTypeDtoList.add(
                    new DataTypeDto(datatypeConfiguration.getKey(),
                            datatypeConfiguration.getUnit(),
                            datatypeConfiguration.getDescription(),
                            datatypeConfiguration.getRtype())
            );
        }
        vehicleJSONPusher.syncDataTypes(dataTypeDtoList);
    }

    public static double calcDistanceBetweenStations(StationDto s1, StationDto s2) {
        double lon1 = Math.toRadians(s1.getLongitude());
        double lon2 = Math.toRadians(s2.getLongitude());
        double lat1 = Math.toRadians(s1.getLatitude());
        double lat2 = Math.toRadians(s2.getLatitude());

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        double r = 6.371 * Math.pow(10, 6);

        return(c * r);
    }


}
