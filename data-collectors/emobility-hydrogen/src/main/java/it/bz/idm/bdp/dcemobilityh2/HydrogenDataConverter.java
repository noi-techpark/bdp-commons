package it.bz.idm.bdp.dcemobilityh2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcemobilityh2.dto.HydrogenDto;
import it.bz.idm.bdp.dto.emobility.ChargingPointsDtoV2;
import it.bz.idm.bdp.dto.emobility.EchargingPlugDto;
import it.bz.idm.bdp.dto.emobility.EchargingStationDto;
import it.bz.idm.bdp.dto.emobility.OutletDtoV2;

@Service
public class HydrogenDataConverter {

    private static final Logger LOG = LogManager.getLogger(HydrogenDataConverter.class.getName());

    public static final String ORIGIN_KEY               = "app.origin";
    public static final String PERIOD_KEY               = "app.period";

    public static final String STATION_PREFIX_KEY       = "app.station.prefix";
    public static final String STATION_TYPE_KEY         = "app.station.type";
    public static final String STATION_RESERVABLE_KEY   = "app.station.reservable";
    public static final String STATION_ACCESS_TYPE_KEY  = "app.station.accessType";
    public static final String STATION_PAYMENT_INFO_KEY = "app.station.paymentInfo";

    public static final String PLUG_TYPE_KEY            = "app.plug.type";
    public static final String PLUG_ID_KEY              = "app.plug.id";
    public static final String PLUG_NAME_KEY            = "app.plug.name";
    public static final String PLUG_AVAILABLE_KEY       = "app.plug.available.state";

    public static final String OUTLET_TYPE_KEY          = "app.outlet.type";

    public static final String POINT_ID_KEY             = "app.point.id";

    @Autowired
    private Environment env;

/*
    <fuelstation>
        <idx>326</idx>
        <name>Bozen (H2 Center)</name>
        <operatorname>iit – Institut für innovative Technologien
        </operatorname>
        <hostname>iit – Institut für innovative Technologien</hostname>
        <street>Via Enrico Mattei</street>
        <streetnr>1</streetnr>
        <zip>39100</zip>
        <city>Bozen</city>
        <countryshortname>IT</countryshortname>
        <latitude>46.475093</latitude>
        <longitude>11.318306</longitude>
        <has_shop>f</has_shop>
        <image>697</image>
        <maintenance_start>2018-06-05 08:30:00</maintenance_start>
        <maintenance_end>2018-06-12 17:00:00</maintenance_end>
        <has_350_large>f</has_350_large>
        <has_350_small>f</has_350_small>
        <operatorhotline>+39 366 578 46 02</operatorhotline>
        <operatorlogo>700</operatorlogo>
        <hostlogo>700</hostlogo>
        <combinedstatus>OPEN</combinedstatus>
        <opening_hours>24 Stunden täglich geöffnet</opening_hours>
        <fundingpage />
        <comments>bla bla</comments>
    </fuelstation>
 */

    public List<HydrogenDto> convertToInternalDTO(List<Map<String, String>> dataMap) throws Exception {
        try {
            LOG.debug("dataMap: "+dataMap);
            if ( dataMap == null ) {
                return null;
            }
            List<HydrogenDto> fetchedData = new ArrayList<HydrogenDto>();
            for (Map<String, String> map : dataMap) {
                EchargingStationDto stationDto = convertMapToStationDto(map);
                EchargingPlugDto plugDto = convertMapToPlugDto(map);
                ChargingPointsDtoV2 pointDto = getPointDto(stationDto, plugDto);
                if ( stationDto!=null && plugDto!=null ) {
                    HydrogenDto dto = new HydrogenDto(stationDto, plugDto, pointDto);
                    fetchedData.add(dto);
                }
            }
            LOG.debug("fetchedData: "+fetchedData);
            return fetchedData;
        } catch (Exception ex) {
            LOG.error("ERROR: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public EchargingStationDto convertMapToStationDto(Map<String, String> map) {
        EchargingStationDto station = null;
        if ( map!=null && map.containsKey("idx") ) {
            station = new EchargingStationDto();

            //From StationDTO
            station.setId(map.get("idx"));
            station.setName(DCUtils.trunc(map.get("name"), 255));
            station.setLongitude(DCUtils.convertStringToDouble(map.get("longitude")));
            station.setLatitude(DCUtils.convertStringToDouble(map.get("latitude")));
            //OMITTED: protected String crs;
            station.setOrigin(DCUtils.trunc(env.getProperty(ORIGIN_KEY), 255));
            station.setMunicipality(DCUtils.trunc(map.get("city"), 255));
            station.setStationType(env.getProperty(STATION_TYPE_KEY));

            //From EchargingStationDto
            //OMITTED: s.setCapacity(dto.getChargingPoints().size());
            station.setProvider(DCUtils.trunc(map.get("hostname"), 255));
            station.setCity(DCUtils.trunc(map.get("city"), 255));
            //The value of "combinedstatus" must be remapped to the corresponding value of the attribute "state"
            station.setState(mapAttribute("app.station.WS.combinedstatus", map.get("combinedstatus"))); 
            station.setPaymentInfo(DCUtils.trunc(env.getProperty(STATION_PAYMENT_INFO_KEY), 255));
            station.setAccessInfo(DCUtils.trunc(map.get("comments"), 255));
            station.setAccessType(env.getProperty(STATION_ACCESS_TYPE_KEY));
            //OMITTED: private String[] categories;
            //OMITTED: private String flashInfo;
            //OMITTED: private String locationServiceInfo;
            station.setAddress(DCUtils.trunc(map.get("street") + " " + map.get("streetnr") + " - " + map.get("zip") + " " + map.get("city") + " - " + map.get("countryshortname"), 255));
            station.setReservable(DCUtils.convertStringToBoolean(env.getProperty(STATION_RESERVABLE_KEY)));
        }

        return station;
    }

    public EchargingPlugDto convertMapToPlugDto(Map<String, String> map) {
        EchargingPlugDto plug = null;
        if ( map.containsKey("idx") ) {
            //For each station we create a Plug with id = station.id-1
            plug = new EchargingPlugDto();
            plug.setId(map.get("idx") + "-" + env.getProperty(PLUG_ID_KEY));
            plug.setLongitude(DCUtils.convertStringToDouble(map.get("longitude")));
            plug.setLatitude(DCUtils.convertStringToDouble(map.get("latitude")));
            plug.setName(DCUtils.trunc(map.get("name")+" - " + env.getProperty(PLUG_NAME_KEY), 255));
            plug.setParentStation(map.get("idx"));
            plug.setOrigin(DCUtils.trunc(env.getProperty(ORIGIN_KEY), 255));
            plug.setStationType(env.getProperty(PLUG_TYPE_KEY));

            //For each Plug we create an Outlet
            OutletDtoV2 outlet = new OutletDtoV2();
            outlet.setId(plug.getId());
            outlet.setOutletTypeCode(map.get(env.getProperty(OUTLET_TYPE_KEY)));
            List<OutletDtoV2> outlets = new ArrayList<OutletDtoV2>();
            outlets.add(outlet);
            plug.setOutlets(outlets);
        }

        return plug;
    }

    public ChargingPointsDtoV2 getPointDto(EchargingStationDto station, EchargingPlugDto plug) {
        ChargingPointsDtoV2 point = null;
        if ( station!=null && plug!=null ) {
            //For each station we create a Point with id = station.id-1
            //String id = station.getId() + "-" + plug.getOutlets().get(0).getId();
            String id = plug.getId();
            point = new ChargingPointsDtoV2();
            point.setId(id);
            point.setState(station.getState());
            point.setOutlets(plug.getOutlets());
            //OMITTED: point.setRechargeState(rechargeState);
        }

        return point;
    }

    public String mapAttribute(String attrName, String wsValue) {
        String dtoValue = env.getProperty(attrName+"."+wsValue);
        if ( dtoValue == null ) {
            String defaultValue = env.getProperty(attrName+".default_value");
            if ( defaultValue != null ) {
                dtoValue = defaultValue;
            }
        }
        return dtoValue;
    }
}
