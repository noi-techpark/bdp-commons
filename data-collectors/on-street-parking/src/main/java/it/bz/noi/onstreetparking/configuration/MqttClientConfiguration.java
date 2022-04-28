package it.bz.noi.onstreetparking.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:mqttclient.properties")
public class MqttClientConfiguration {

	@Value( "${mqttclient.serverURI}" )
    private String serverURI;

    @Value( "${mqttclient.username}" )
    private String username;

    @Value( "${mqttclient.password}" )
    private String password;

    @Value( "${mqttclient.clientId}" )
    private String clientId;

    @Value( "${mqttclient.topic}" )
    private String topic;

	public String getServerURI() {
		return serverURI;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getClientId() {
		return clientId;
	}

	public String getTopic() {
		return topic;
	}
}
