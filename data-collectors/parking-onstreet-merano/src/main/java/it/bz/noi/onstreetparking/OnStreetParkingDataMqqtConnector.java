// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.onstreetparking;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.bz.noi.onstreetparking.configuration.MqttClientConfiguration;
import it.bz.noi.onstreetparking.dto.ParkingData;
import it.bz.noi.onstreetparking.dto.Position;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;

@Service
public class OnStreetParkingDataMqqtConnector implements MqttCallback {

	private static final Logger LOG = LoggerFactory.getLogger(OnStreetParkingDataMqqtConnector.class);
	private static final String MQTT_CLIENT_SYNCRONIZATION_BLOCK = "MQTT_CLIENT_SYNCRONIZATION_BLOCK";

	@Autowired
	private MqttClientConfiguration mqttClientConfiguration;

	private MqttClient mqttClient;
	private IMqttMessageArrivedCallback mqttMessageArrivedCallback;

	public void setMqttMessageArrivedCallback(IMqttMessageArrivedCallback mqttMessageArrivedCallback) {
		this.mqttMessageArrivedCallback = mqttMessageArrivedCallback;
	}

	public synchronized void connect() throws MqttException {
		LOG.info("Connector connect");

		synchronized (MQTT_CLIENT_SYNCRONIZATION_BLOCK) {
			if (mqttClient == null) {
				mqttClient = new MqttClient(mqttClientConfiguration.getServerURI(),
					mqttClientConfiguration.getClientId());

				mqttClient.setCallback(this);
			}

			if(!mqttClient.isConnected()) {
				MqttConnectOptions connOpts = new MqttConnectOptions();
				connOpts.setCleanSession(true);
				connOpts.setConnectionTimeout(10);
				connOpts.setUserName(mqttClientConfiguration.getUsername());
				connOpts.setPassword(mqttClientConfiguration.getPassword().toCharArray());

				LOG.debug("connect to mqtt client");
				mqttClient.connect(connOpts);
				LOG.debug("subscribe to topic: {}", mqttClientConfiguration.getTopic());
				mqttClient.subscribe(mqttClientConfiguration.getTopic());
			} else {
				LOG.debug("mqttClient connected -> do not connect");
			}
		}
	}

	@Override
	public void connectionLost(Throwable throwable) {
		LOG.info("connectionLost");
	}

	@Override
	public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
		LOG.info("messageArrived");
		LOG.debug("messageArrived: {} - {}", s, mqttMessage.toString());

		parseMessage(mqttMessage.toString());
	}

	public void parseMessage(String mqttMessageString) throws IOException {
		// parse message
		ParkingData parkingData = new ParkingData();

		try {
			JsonObject jsonObject = new Gson().fromJson(mqttMessageString, JsonObject.class);

			parkingData.setType(extractString(jsonObject, "type"));

			JsonObject dataNode = jsonObject.getAsJsonObject("data");

			parkingData.setGuid(extractString(dataNode, "guid"));
			parkingData.setName(extractString(dataNode, "name"));
			parkingData.setState(extractString(dataNode, "state"));
			parkingData.setLastChange(extractDate(dataNode, "last_change"));

			JsonObject positionNode = dataNode.getAsJsonObject("position");
			Position position = new Position();
			position.setLatitude(extractDouble(positionNode, "latitude"));
			position.setLongitude(extractDouble(positionNode, "longitude"));
			parkingData.setPosition(position);
		} catch (Exception e) {
			// throw an error in case not even the top level element cannot be extracted as expected
			LOG.warn("---");
			LOG.warn("messageArrived() ERROR: unparsable mqtt message:");
			LOG.warn("vvv");
			LOG.warn(mqttMessageString);
			LOG.warn("^^^");
			LOG.warn(e.getMessage(), e);
			LOG.warn("---");
			throw new IOException("ERROR: unparsable mqtt message");
		}

		if(mqttMessageArrivedCallback != null) {
			mqttMessageArrivedCallback.onParkingDataArrived(parkingData);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		throw new UnsupportedOperationException();
	}

	public static Double extractDouble(JsonObject jsonObject, String prop) {
		JsonElement ret = jsonObject.get(prop);
		if (ret == null || ret.isJsonNull())
			return null;
		return ret.getAsDouble();
	}

	public static String extractString(JsonObject jsonObject, String prop) {
		JsonElement ret = jsonObject.get(prop);
		if (ret == null || ret.isJsonNull())
			return null;
		return ret.getAsString();
	}

	public static ZonedDateTime extractDate(JsonObject jsonObject, String prop) {
		JsonElement ret = jsonObject.get(prop);
		if (ret == null || ret.isJsonNull())
			return null;
		return ZonedDateTime.parse(ret.getAsString());
	}
}
