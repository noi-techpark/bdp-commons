package it.bz.noi.ondemandmerano.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:connector.properties")
public class ConnectorConfiguration {

    @Value( "${connector.username}" )
    private String username;

    @Value( "${connector.password}" )
    private String password;

    @Value( "${connector.url}" )
    private String url;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getURL() {
        return url;
    }
}
