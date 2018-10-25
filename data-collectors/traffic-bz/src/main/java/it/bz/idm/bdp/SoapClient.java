package it.bz.idm.bdp;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.tempuri.ArrayOfInt;
import org.tempuri.GetClassifConfig;
import org.tempuri.GetClassifConfigResponse;
import org.tempuri.GetData;
import org.tempuri.GetDataHistorical;
import org.tempuri.GetDataHistoricalResponse;
import org.tempuri.GetDataResponse;
import org.tempuri.GetDataTypes;
import org.tempuri.GetDataTypesResponse;
import org.tempuri.GetMetadataStation;
import org.tempuri.GetMetadataStationResponse;
import org.tempuri.GetStationID;
import org.tempuri.GetStationIDResponse;

import cleanroadsdatatype.cleanroadswebservices.GetClassifConfigResult;
import cleanroadsdatatype.cleanroadswebservices.GetClassifConfigResult.XmlClassificazione;
import cleanroadsdatatype.cleanroadswebservices.GetDataHistoricalResult;
import cleanroadsdatatype.cleanroadswebservices.GetDataResult;
import cleanroadsdatatype.cleanroadswebservices.GetDataResult.XmlRwData;
import cleanroadsdatatype.cleanroadswebservices.GetDataTypesResult;
import cleanroadsdatatype.cleanroadswebservices.GetDataTypesResult.XmlDataType;
import cleanroadsdatatype.cleanroadswebservices.GetMetadataStationResult;

@Service
public class SoapClient {

	@Autowired
	private WebServiceTemplate webServiceTemplate;

	public Object getData(Object requestPayload){
		Object responsePayload = webServiceTemplate.marshalSendAndReceive(requestPayload);
		if (responsePayload == null)
			throw new IllegalStateException("Webservice not working correctly");
		return responsePayload;
	}

	public List<Integer> getStationIdentifiers() {
		GetStationID getStationsPayload = new GetStationID();
		Object responsePayload = this.getData(getStationsPayload);
		if (responsePayload != null && responsePayload instanceof GetStationIDResponse && !((GetStationIDResponse)responsePayload).getGetStationIDResult().getInt().isEmpty())
			return ((GetStationIDResponse)responsePayload).getGetStationIDResult().getInt();
		throw new IllegalStateException("Unexpected webservice response");
	}

	public GetMetadataStationResult getStationMetaData(Integer station) {
		GetMetadataStation metaDataPayload = new GetMetadataStation();
		metaDataPayload.setStationId(station);
		Object responsePayload = this.getData(metaDataPayload);
		if (responsePayload != null && responsePayload instanceof GetMetadataStationResponse) {
			GetMetadataStationResult metadataStationResult = ((GetMetadataStationResponse)responsePayload).getGetMetadataStationResult();
			if (metadataStationResult != null)
				return metadataStationResult;
		}
		throw new IllegalStateException("Unexpected webservice response with paramater station = "+station);
	}

	public List<XmlClassificazione> getStationConfig(Integer station) {
		GetClassifConfig configPayload = new GetClassifConfig();
		configPayload.setStationId(station);
		Object responsePayload = this.getData(configPayload);
		if (responsePayload != null && responsePayload instanceof GetClassifConfigResponse) {
			GetClassifConfigResult configResult = ((GetClassifConfigResponse)responsePayload).getGetClassifConfigResult();
			if (configResult != null && configResult.getXmlClassificazione() !=null && !configResult.getXmlClassificazione().isEmpty())
				return  configResult.getXmlClassificazione();
		}
		throw new IllegalStateException("Unexpected webservice response with paramater station = "+station);

	}

	public List<XmlDataType> getStationDataTypes(Integer station) {
		GetDataTypes getDataTypesPayload = new GetDataTypes();
		getDataTypesPayload.setStationId(station);
		Object responsePayload = this.getData(getDataTypesPayload);
		if (responsePayload != null && responsePayload instanceof GetDataTypesResponse) {
			GetDataTypesResult getDataTypesResult = ((GetDataTypesResponse)responsePayload).getGetDataTypesResult();
			if ( getDataTypesResult != null && getDataTypesResult.getXmlDataType()!= null )
				return getDataTypesResult.getXmlDataType();
		}
		throw new IllegalStateException("Unexpected webservice response with paramater station = "+station);
	}

	public List<XmlRwData> getCurrentData(Integer station,
			List<XmlDataType> stationDataTypes) {
		GetData getDataPayload = new GetData();
		getDataPayload.setStationId(station);
		ArrayOfInt values = new ArrayOfInt();
		for(XmlDataType type:stationDataTypes)
			values.getInt().add(type.getId());
		getDataPayload.setDataTypeList(values);
		Object responsePayload = this.getData(getDataPayload);
		if (responsePayload != null && responsePayload instanceof GetDataResponse) {
			GetDataResult getDataResult = ((GetDataResponse) responsePayload).getGetDataResult();
			if (getDataResult != null && getDataResult.getXmlRwData() != null)
				return getDataResult.getXmlRwData();
		}
		throw new IllegalStateException("Unexpected webservice response with paramater station = " + station);
	}

	public List<cleanroadsdatatype.cleanroadswebservices.GetDataHistoricalResult.XmlRwData> getHistoryData(Integer station,
			List<XmlDataType> stationDataTypes, XMLGregorianCalendar from,
			XMLGregorianCalendar to) {
		GetDataHistorical getHistoryPayload = new GetDataHistorical();
		getHistoryPayload.setStationId(station);
		ArrayOfInt values = new ArrayOfInt();
		for(XmlDataType type:stationDataTypes)
			values.getInt().add(type.getId());
		getHistoryPayload.setDataTypeList(values);

		getHistoryPayload.setUtcTimeFrom(from);
		getHistoryPayload.setUtcTimeTo(to);
		Object responsePayload = this.getData(getHistoryPayload);
		if (responsePayload != null && responsePayload instanceof GetDataHistoricalResponse) {
			GetDataHistoricalResult getDataHistoricalResult = ((GetDataHistoricalResponse)responsePayload).getGetDataHistoricalResult();
			if (getDataHistoricalResult != null && getDataHistoricalResult.getXmlRwData() != null && !getDataHistoricalResult.getXmlRwData().isEmpty())
				return getDataHistoricalResult.getXmlRwData();
				
		}
		throw new IllegalStateException("Unexpected webservice response with paramater station = " + station);
	}

}
