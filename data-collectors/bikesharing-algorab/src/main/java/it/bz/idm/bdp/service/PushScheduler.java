package it.bz.idm.bdp.service;


import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@Service
public class PushScheduler{
	
	
	@Autowired
	private DataRetriever retriever;
	
	@Autowired
	private BikesharingPusher pusher;
	
	private static final String BICYCLE_DS = "Bicycle";

	public void syncStations(){
		try {
			StationList stations = retriever.retrieveStations();
			pusher.syncStations(stations);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void syncBikes(){
		try {
			StationList bicycles = retriever.retrieveBicycles();
			pusher.syncStations(BICYCLE_DS,bicycles);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void pushStationData(){
		try {
			DataMapDto<RecordDtoImpl> currentState = retriever.retrieveCurrentState();
			pusher.pushData(currentState);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void pushBicycleData(){
		try {
			DataMapDto<RecordDtoImpl> currentState = retriever.retrieveBicyclesData();
			pusher.pushData(BICYCLE_DS, currentState);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
