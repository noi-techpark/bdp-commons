package it.bz.idm.bdp.rwis;

import org.tempuri.ArrayOfInt;
import org.tempuri.GetData;
import org.tempuri.GetDataTest;
import org.tempuri.GetDataTypes;
import org.tempuri.GetMetadataStation;

import cleanroadsdatatype.cleanroadswebservices.GetDataResult;
import cleanroadsdatatype.cleanroadswebservices.GetDataTestResult;
import cleanroadsdatatype.cleanroadswebservices.GetDataTypesResult;
import cleanroadsdatatype.cleanroadswebservices.GetMetadataStationResult;

public class RWISFetch {
    public static ArrayOfInt getStationID() {
        org.tempuri.InfoMobility service = new org.tempuri.InfoMobility();
        org.tempuri.InfoMobilitySoap port = service.getInfoMobilitySoap();
        return port.getStationID();
    }

    public static GetMetadataStationResult getMetadataStation(int stationId) {
        org.tempuri.InfoMobility service = new org.tempuri.InfoMobility();
        org.tempuri.InfoMobilitySoap port = service.getInfoMobilitySoap();
        GetMetadataStation metaData = new GetMetadataStation();
        metaData.setStationId(stationId);
        return port.getMetadataStation(metaData).getGetMetadataStationResult();
    }

    public static GetDataResult getData(int idStazione, org.tempuri.ArrayOfInt dataTypeList) {
        org.tempuri.InfoMobility service = new org.tempuri.InfoMobility();
        org.tempuri.InfoMobilitySoap port = service.getInfoMobilitySoap();
        GetData data = new GetData();
        data.setDataTypeList(dataTypeList);
        data.setIdStazione(idStazione);
        return port.getData(data).getGetDataResult();
    }

    public static GetDataTypesResult getDataTypes() {
        org.tempuri.InfoMobility service = new org.tempuri.InfoMobility();
        org.tempuri.InfoMobilitySoap port = service.getInfoMobilitySoap();
        GetDataTypes parameters = new GetDataTypes();
		return port.getDataTypes(parameters).getGetDataTypesResult();
    }

    public static GetDataTestResult getDataTest(int idStazione) {
        org.tempuri.InfoMobility service = new org.tempuri.InfoMobility();
        org.tempuri.InfoMobilitySoap port = service.getInfoMobilitySoap();
        GetDataTest parameter = new GetDataTest();
		return port.getDataTest(parameter).getGetDataTestResult();
    }
}
