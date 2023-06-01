// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.bikechargers.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:connector.properties")
public class ConnectorConfiguration {

    @Value( "${connector.apiKey.name}" )
    private String apiKeyName;

    @Value( "${connector.apiKey.value}" )
    private String apiKeyValue;

    @Value( "${connector.url}" )
    private String url;

    public String getApiKeyName() {
        return apiKeyName;
    }

    public String getApiKeyValue() {
        return apiKeyValue;
    }

    public String getURL() {
        return url;
    }
}
