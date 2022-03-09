package it.bz.idm.bdp;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.tempuri.GetData;
import org.tempuri.GetDataHistorical;
import org.tempuri.GetDataHistoricalResponse;
import org.tempuri.GetDataResponse;
import org.tempuri.GetListStationID;
import org.tempuri.GetListStationIDResponse;
import org.tempuri.GetMetadataStation;
import org.tempuri.GetMetadataStationResponse;
import org.tempuri.xmlresponewebservice.GetDataHistoricalResult;
import org.tempuri.xmlresponewebservice.GetDataHistoricalResult.XmlRwData;
import org.tempuri.xmlresponewebservice.GetDataResult;
import org.tempuri.xmlresponewebservice.GetMetadataStationResult;

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
		GetListStationID getStationsPayload = new GetListStationID();
		Object responsePayload = this.getData(getStationsPayload);
		if (responsePayload instanceof GetListStationIDResponse && !((GetListStationIDResponse)responsePayload).getGetListStationIDResult().getInt().isEmpty())
			return ((GetListStationIDResponse)responsePayload).getGetListStationIDResult().getInt();
		throw new IllegalStateException("Unexpected webservice response");
	}

	public GetMetadataStationResult getStationMetaData(Integer station) {
		GetMetadataStation metaDataPayload = new GetMetadataStation();
		metaDataPayload.setStationId(station);
		Object responsePayload = this.getData(metaDataPayload);
		if (responsePayload instanceof GetMetadataStationResponse) {
			GetMetadataStationResult metadataStationResult = ((GetMetadataStationResponse)responsePayload).getGetMetadataStationResult();
			if (metadataStationResult != null)
				return metadataStationResult;
		}
		throw new IllegalStateException("Unexpected webservice response with paramater station = "+station);
	}

	public List<org.tempuri.xmlresponewebservice.GetDataResult.XmlRwData> getCurrentData(Integer station) {
		GetData getDataPayload = new GetData();
		getDataPayload.setStationId(station);
		Object responsePayload = this.getData(getDataPayload);
		if (responsePayload instanceof GetDataResponse) {
			GetDataResult getDataResult = ((GetDataResponse) responsePayload).getGetDataResult();
			if (getDataResult != null && getDataResult.getXmlRwData() != null)
				return getDataResult.getXmlRwData();
		}
		throw new IllegalStateException("Unexpected webservice response with paramater station = " + station);
	}

	public List<XmlRwData> getHistoryData(Integer station, XMLGregorianCalendar from,
			XMLGregorianCalendar to) {
		GetDataHistorical getHistoryPayload = new GetDataHistorical();
		getHistoryPayload.setStationId(station);
		getHistoryPayload.setUtcTimeFrom(from);
		getHistoryPayload.setUtcTimeTo(to);
		Object responsePayload = this.getData(getHistoryPayload);
		if (responsePayload instanceof GetDataHistoricalResponse) {
			GetDataHistoricalResult getDataHistoricalResult = ((GetDataHistoricalResponse)responsePayload).getGetDataHistoricalResult();
			if (getDataHistoricalResult != null && getDataHistoricalResult.getXmlRwData() != null && !getDataHistoricalResult.getXmlRwData().isEmpty())
				return getDataHistoricalResult.getXmlRwData();

		}
		throw new IllegalStateException("Unexpected webservice response with paramater station = " + station);
	}

}
